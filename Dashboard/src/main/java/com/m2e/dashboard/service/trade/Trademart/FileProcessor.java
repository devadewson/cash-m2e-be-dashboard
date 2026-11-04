package com.m2e.dashboard.service.trade.Trademart;


import com.m2e.dashboard.model.DataMart.DatamartAgreement;
import com.m2e.dashboard.repository.DatamartAgreementRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Component
@Slf4j
@RequiredArgsConstructor
public class FileProcessor {

    private final DataMapper mapper;
    private final DatamartAgreementRepository repository;

    @PersistenceContext
    private EntityManager entityManager;

    private static final int BATCH_SIZE = 500;

    @Transactional
    public int process(String filePath) throws IOException {
        long start = System.currentTimeMillis();

        Path path = Path.of(filePath);
        if (!Files.exists(path)) {
            throw new IOException("File tidak ditemukan: " + filePath);
        }

        LocalDateTime processDate = LocalDateTime.now();
        LocalDateTime startOfDay = processDate.toLocalDate().atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1);

        int deleted = repository.deleteByProcessDateRange(startOfDay, endOfDay);
        if (deleted > 0) {
            log.info("[TRADE-SFTP] {} baris hari ini dihapus sebelum insert ulang", deleted);
        }

        List<String> batch = new ArrayList<>(BATCH_SIZE);
        int lineNo = 1;
        int batchFirstLine = 2;
        int dataLines = 0;
        int inserted = 0;

        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.ISO_8859_1)) {
            reader.readLine(); // baris 1 selalu header

            String line;
            while ((line = reader.readLine()) != null) {
                lineNo++;
                if (line.isBlank()) {
                    continue;
                }
                if (batch.isEmpty()) {
                    batchFirstLine = lineNo;
                }
                batch.add(line);
                dataLines++;

                if (batch.size() >= BATCH_SIZE) {
                    inserted += saveBatch(batch, processDate, batchFirstLine);
                    batch.clear();
                }
            }
        }

        if (!batch.isEmpty()) {
            inserted += saveBatch(batch, processDate, batchFirstLine);
        }

        if (inserted == 0 && dataLines > 0) {
            // Rollback: penghapusan di atas ikut dibatalkan, data sebelumnya tetap ada
            throw new IllegalStateException("File trade berisi " + dataLines
                    + " baris tapi tidak ada yang valid. Cek format file.");
        }

        log.info("[TRADE-SFTP] Selesai: {} baris data, {} tersimpan, {} dilewati, dalam {} ms",
                dataLines, inserted, dataLines - inserted, System.currentTimeMillis() - start);
        return inserted;
    }

    private int saveBatch(List<String> lines, LocalDateTime processDate, int firstLineNo) {
        List<DatamartAgreement> records = mapper.map(lines, processDate, firstLineNo);
        if (records.isEmpty()) {
            return 0;
        }
        repository.saveAll(records);
        entityManager.flush();
        entityManager.clear();
        return records.size();
    }
}
