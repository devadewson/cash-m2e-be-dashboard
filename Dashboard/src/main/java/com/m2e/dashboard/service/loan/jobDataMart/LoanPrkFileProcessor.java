package com.m2e.dashboard.service.loan.jobDataMart;

import com.m2e.dashboard.model.DataMart.LoanPrkEntity;
import com.m2e.dashboard.repository.LoanPrkRepository;
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
import java.util.List;

/**
 * Parse file EDW_LOAN_PRK.TXT lalu simpan ke DATAMART_LOAN_PRK.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LoanPrkFileProcessor {

    private final LoanPrkRepository repository;

    private static final DateTimeFormatter FILE_DATE = DateTimeFormatter.ofPattern("yyyyMMdd");

    /** Placeholder null di file EDW, bukan tanggal sungguhan. */
    private static final String NULL_DATE = "00010101";

    private static final int BATCH_SIZE = 500;
    private static final int EXPECTED_FIELDS = 10;
    private static final int MAX_CURRENCY_LENGTH = 3;

    @Transactional
    public int process(String localFilePath) throws IOException {
        Path path = Path.of(localFilePath);
        if (!Files.exists(path)) {
            throw new IOException("File tidak ditemukan: " + localFilePath);
        }

        LocalDateTime processDate = LocalDateTime.now();
        LocalDateTime startOfDay = processDate.toLocalDate().atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1);

        int deleted = repository.deleteByProcessDateRange(startOfDay, endOfDay);
        repository.flush();
        if (deleted > 0) {
            log.info("[PRK-SFTP] {} baris hari ini dihapus sebelum insert ulang", deleted);
        }

        List<LoanPrkEntity> batch = new ArrayList<>(BATCH_SIZE);
        int lineNo = 1;
        int inserted = 0;
        int skipped = 0;

        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.ISO_8859_1)) {
            reader.readLine(); // baris 1 selalu header dari EDW

            String line;
            while ((line = reader.readLine()) != null) {
                lineNo++;

                if (line.isBlank()) {
                    continue;
                }

                LoanPrkEntity entity = parseLine(line, processDate, lineNo);
                if (entity == null) {
                    skipped++;
                    continue;
                }

                batch.add(entity);
                if (batch.size() >= BATCH_SIZE) {
                    repository.saveAll(batch);
                    repository.flush();
                    inserted += batch.size();
                    batch.clear();
                }
            }
        }

        if (!batch.isEmpty()) {
            repository.saveAll(batch);
            inserted += batch.size();
        }

        log.info("[PRK-SFTP] Selesai: {} baris dibaca, {} tersimpan, {} dilewati",
                lineNo, inserted, skipped);

        if (inserted == 0 && lineNo > 1) {
            log.error("[PRK-SFTP] {} baris dibaca tapi tidak ada yang tersimpan. "
                    + "Cek format file dan log warning di atas.", lineNo);
        }

        return inserted;
    }

    private LoanPrkEntity parseLine(String line, LocalDateTime processDate, int lineNo) {
        // -1 agar field kosong di akhir tetap dihitung
        String[] f = line.split("\\|", -1);

        if (f.length < EXPECTED_FIELDS) {
            if (lineNo <= 3) {
                log.error("[PRK-SFTP] Baris {} hanya terpecah jadi {} kolom. Isi: [{}]",
                        lineNo, f.length, line);
            } else {
                log.warn("[PRK-SFTP] Baris {} hanya punya {} kolom, dilewati", lineNo, f.length);
            }
            return null;
        }

        // Urutan: BASE_DT|CIF_NO|ACCOUNT|CURRENCY|LIMIT|OUTSTANDING|MATURITY|
        //         INTEREST_DUE|AVAILABLE_LIMIT_AWAL|LIMIT_AWAL
        String cifNo = f[1].trim();
        String accountNo = f[2].trim();

        if (cifNo.isEmpty() || accountNo.isEmpty()) {
            log.warn("[PRK-SFTP] Baris {} tanpa CIF atau nomor rekening, dilewati", lineNo);
            return null;
        }

        String currency = f[3].trim().toUpperCase();
        if (currency.length() > MAX_CURRENCY_LENGTH) {
            log.warn("[PRK-SFTP] Baris {}: currency [{}] tidak valid, dilewati. Isi: [{}]",
                    lineNo, currency, line);
            return null;
        }

        LoanPrkEntity e = new LoanPrkEntity();
        e.setBaseDate(parseDate(f[0], lineNo));
        e.setCifNo(cifNo);
        e.setAccountNo(accountNo);
        e.setCurrency(currency.isEmpty() ? null : currency);
        e.setLimitAmount(parseDecimal(f[4], lineNo));
        e.setOutstanding(parseDecimal(f[5], lineNo));
        e.setMaturityDate(parseDate(f[6], lineNo));
        e.setInterestDue(parseDecimal(f[7], lineNo));
        e.setAvailableLimitAwal(parseDecimal(f[8], lineNo));
        e.setLimitAwal(parseDecimal(f[9], lineNo));
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
            log.warn("[PRK-SFTP] Baris {}: format tanggal tidak valid [{}]", lineNo, v);
            return null;
        }
    }

    /** Nilai punya spasi di depan dan bisa negatif, misal " -000008413981874.75". */
    private BigDecimal parseDecimal(String value, int lineNo) {
        String v = (value == null) ? "" : value.trim();
        if (v.isEmpty()) {
            return BigDecimal.ZERO;
        }
        try {
            return new BigDecimal(v);
        } catch (NumberFormatException ex) {
            log.warn("[PRK-SFTP] Baris {}: format angka tidak valid [{}]", lineNo, v);
            return BigDecimal.ZERO;
        }
    }
}