package com.m2e.dashboard.service.loan.plafond;

import com.m2e.dashboard.model.DataMart.LoanPlafondEntity;
import com.m2e.dashboard.repository.LoanPlafondRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class LoanPlafondFileProcessor {

    private final LoanPlafondRepository repository;

    private static final DateTimeFormatter FILE_DATE = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final String NULL_DATE = "00010101";
    private static final int BATCH_SIZE = 500;
    private static final int EXPECTED_FIELDS = 3;
    private static final int MAX_CIF_LENGTH = 20;

    @Transactional
    public int process(String localFilePath) throws IOException {
        Path path = Path.of(localFilePath);
        if (!Files.exists(path)) {
            throw new IOException("File tidak ditemukan: " + localFilePath);
        }

        LocalDateTime processDate = LocalDateTime.now();
        LocalDateTime startOfDay = processDate.toLocalDate().atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1);

        Map<String, LoanPlafondEntity> byCif = new LinkedHashMap<>();
        int lineNo = 1;
        int dataLines = 0;
        int skipped = 0;
        int duplicates = 0;

        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.ISO_8859_1)) {
            reader.readLine(); // baris 1 selalu header

            String line;
            while ((line = reader.readLine()) != null) {
                lineNo++;
                if (line.isBlank()) {
                    continue;
                }
                dataLines++;

                LoanPlafondEntity entity = parseLine(line, processDate, lineNo);
                if (entity == null) {
                    skipped++;
                    continue;
                }

                LoanPlafondEntity previous = byCif.put(entity.getCifNo(), entity);
                if (previous != null) {
                    duplicates++;
                    log.warn("[PLAFOND-SFTP] Baris {}: CIF {} muncul lagi, nilai {} diganti {}",
                            lineNo, entity.getCifNo(), previous.getPlafond(), entity.getPlafond());
                }
            }
        }

        if (byCif.isEmpty()) {
            if (dataLines > 0) {
                // Rollback: data hari ini (kalau ada) tetap utuh
                throw new IllegalStateException("File plafond berisi " + dataLines
                        + " baris tapi tidak ada yang valid. Cek format file.");
            }
            log.warn("[PLAFOND-SFTP] File hanya berisi header, tidak ada data yang dimuat");
            return 0;
        }

        int deleted = repository.deleteByProcessDateRange(startOfDay, endOfDay);
        repository.flush();
        if (deleted > 0) {
            log.info("[PLAFOND-SFTP] {} baris hari ini dihapus sebelum insert ulang", deleted);
        }

        List<LoanPlafondEntity> batch = new ArrayList<>(BATCH_SIZE);
        int inserted = 0;
        for (LoanPlafondEntity entity : byCif.values()) {
            batch.add(entity);
            if (batch.size() >= BATCH_SIZE) {
                repository.saveAll(batch);
                repository.flush();
                inserted += batch.size();
                batch.clear();
            }
        }
        if (!batch.isEmpty()) {
            repository.saveAll(batch);
            inserted += batch.size();
        }

        log.info("[PLAFOND-SFTP] Selesai: {} baris data, {} tersimpan, {} dilewati, {} CIF ganda",
                dataLines, inserted, skipped, duplicates);
        return inserted;
    }

    private LoanPlafondEntity parseLine(String line, LocalDateTime processDate, int lineNo) {
        String[] f = line.split("\\|", -1);

        if (f.length < EXPECTED_FIELDS) {
            log.warn("[PLAFOND-SFTP] Baris {} hanya punya {} kolom, dilewati. Isi: [{}]",
                    lineNo, f.length, line);
            return null;
        }

        String cifNo = f[1].trim();
        if (cifNo.isEmpty() || cifNo.length() > MAX_CIF_LENGTH) {
            log.warn("[PLAFOND-SFTP] Baris {}: CIF [{}] tidak valid, dilewati", lineNo, cifNo);
            return null;
        }

        BigDecimal plafond = parseDecimal(f[2]);
        if (plafond == null) {
            log.warn("[PLAFOND-SFTP] Baris {}: plafond [{}] bukan angka, dilewati", lineNo, f[2]);
            return null;
        }

        LoanPlafondEntity e = new LoanPlafondEntity();
        e.setBaseDate(parseDate(f[0], lineNo));
        e.setCifNo(cifNo);
        e.setPlafond(plafond);
        e.setProcessDate(processDate);
        e.setCreatedDate(LocalDateTime.now());
        return e;
    }

    private LocalDate parseDate(String value, int lineNo) {
        String v = (value == null) ? "" : value.trim();
        if (v.isEmpty() || NULL_DATE.equals(v)) {
            return null;
        }
        try {
            return LocalDate.parse(v, FILE_DATE);
        } catch (DateTimeParseException ex) {
            log.warn("[PLAFOND-SFTP] Baris {}: BASE_DT tidak valid [{}]", lineNo, v);
            return null;
        }
    }

    private BigDecimal parseDecimal(String value) {
        String v = (value == null) ? "" : value.trim();
        if (v.isEmpty()) {
            return null;
        }
        try {
            return new BigDecimal(v);
        } catch (NumberFormatException ex) {
            return null;
        }
    }
}

