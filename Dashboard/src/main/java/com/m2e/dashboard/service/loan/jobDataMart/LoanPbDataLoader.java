package com.m2e.dashboard.service.loan.jobDataMart;


import com.m2e.dashboard.model.DataMart.LoanPbBaseEntity;
import com.m2e.dashboard.model.DataMart.LoanPbDrawEntity;
import com.m2e.dashboard.model.DataMart.LoanPbFacilityEntity;
import com.m2e.dashboard.repository.LoanPbDrawRepository;
import com.m2e.dashboard.repository.LoanPbFacilityRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * Memuat file fasilitas dan draw ke database dalam SATU transaksi.
 * Gagal di mana pun, kedua tabel di-rollback sehingga tidak pernah setengah jadi.
 *
 * Harus bean terpisah dari pemanggilnya: @Transactional tidak berlaku
 * untuk pemanggilan dari method lain di kelas yang sama.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LoanPbDataLoader {

    private final LoanPbFacilityRepository facilityRepository;
    private final LoanPbDrawRepository drawRepository;
    private final LoanPbLineParser parser;

    @PersistenceContext
    private EntityManager entityManager;

    private static final int BATCH_SIZE = 500;
    private static final String FACILITY_DRAW_NO = "999";

    public record LoadResult(int facilityRows, int drawRows) {}

    @Transactional
    public LoadResult load(String facilityFile, String drawFile) throws IOException {
        LocalDateTime processDate = LocalDateTime.now();
        LocalDateTime startOfDay = processDate.toLocalDate().atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1);

        // Hanya data hari ini; riwayat tanggal lain tetap tersimpan
        int deletedFacility = facilityRepository.deleteByProcessDateRange(startOfDay, endOfDay);
        int deletedDraw = drawRepository.deleteByProcessDateRange(startOfDay, endOfDay);
        if (deletedFacility > 0 || deletedDraw > 0) {
            log.info("[PB-SFTP] Data hari ini dihapus sebelum insert ulang: fasilitas={} draw={}",
                    deletedFacility, deletedDraw);
        }

        int facilityRows = loadFile(facilityFile, LoanPbFacilityEntity::new,
                facilityRepository, processDate, true, "PB-FACILITY");

        int drawRows = loadFile(drawFile, LoanPbDrawEntity::new,
                drawRepository, processDate, false, "PB-DRAW");

        return new LoadResult(facilityRows, drawRows);
    }

    private <T extends LoanPbBaseEntity> int loadFile(String filePath, Supplier<T> factory,
                                                      JpaRepository<T, Long> repository,
                                                      LocalDateTime processDate,
                                                      boolean facilityFile, String tag)
            throws IOException {

        Path path = Path.of(filePath);
        if (!Files.exists(path)) {
            throw new IOException("File tidak ditemukan: " + filePath);
        }

        List<T> batch = new ArrayList<>(BATCH_SIZE);
        int lineNo = 1;
        int parsed = 0;
        int inserted = 0;
        int skipped = 0;
        int wrongType = 0;

        // ISO_8859_1 menerima semua byte 0-255, konsisten di Windows maupun Linux
        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.ISO_8859_1)) {
            reader.readLine(); // baris 1 selalu header dari EDW

            String line;
            while ((line = reader.readLine()) != null) {
                lineNo++;

                if (line.isBlank()) {
                    continue;
                }

                T entity = parser.parse(line, factory, processDate, lineNo, tag);
                if (entity == null) {
                    skipped++;
                    continue;
                }
                parsed++;

                // File fasilitas wajib DRAW_NO 999, file draw wajib bukan 999
                boolean isFacilityRow = FACILITY_DRAW_NO.equals(entity.getDrawNo());
                if (isFacilityRow != facilityFile) {
                    wrongType++;
                    log.warn("[{}] Baris {}: DRAW_NO [{}] tidak sesuai jenis file, dilewati",
                            tag, lineNo, entity.getDrawNo());
                    continue;
                }

                batch.add(entity);
                if (batch.size() >= BATCH_SIZE) {
                    inserted += flushBatch(repository, batch);
                }
            }
        }

        if (!batch.isEmpty()) {
            inserted += flushBatch(repository, batch);
        }

        if (parsed > 0 && wrongType == parsed) {
            throw new IllegalStateException("Seluruh baris di " + path.getFileName()
                    + " tidak sesuai jenis file. Kemungkinan file fasilitas dan draw tertukar.");
        }

        log.info("[{}] Selesai: {} baris dibaca, {} tersimpan, {} dilewati, {} salah jenis",
                tag, lineNo, inserted, skipped, wrongType);

        return inserted;
    }

    /** Simpan satu batch lalu kosongkan persistence context agar memori tidak menumpuk. */
    private <T> int flushBatch(JpaRepository<T, Long> repository, List<T> batch) {
        int size = batch.size();
        repository.saveAll(batch);
        entityManager.flush();
        entityManager.clear();
        batch.clear();
        return size;
    }
}
