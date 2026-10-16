package com.m2e.dashboard.service.deposit.fd.Rest;

import com.m2e.dashboard.model.DataMart.DepositFdEntity;
import com.m2e.dashboard.repository.DepositFdRepository;
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
 * Parse file EDW_LOAN_FD.TXT lalu simpan ke DATAMART_DEPOSIT_FD.
 *
 * Format pipe-delimited, baris pertama selalu header:
 * CIF_NO|DEPOSIT_NUMBER|TANGGAL_PLACEMENT|MATURITY_DATE|CURRENCY|PRINCIPAL|TENOR|INTEREST_RATE
 *
 * Data tanggal lain tetap tersimpan; hanya data hari ini yang dihapus
 * sebelum insert, supaya rerun tidak menggandakan baris.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DepositFdFileProcessor {

    private final DepositFdRepository repository;

    private static final DateTimeFormatter FILE_DATE = DateTimeFormatter.ofPattern("yyyyMMdd");

    /** Placeholder null di file EDW, bukan tanggal sungguhan. */
    private static final String NULL_DATE = "00010101";

    private static final int BATCH_SIZE = 500;
    private static final int EXPECTED_FIELDS = 8;
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

        // Hapus data hari ini saja, data tanggal sebelumnya tetap tersimpan
        int deleted = repository.deleteByProcessDateRange(startOfDay, endOfDay);
        repository.flush();
        if (deleted > 0) {
            log.info("[FD-SFTP] {} baris hari ini dihapus sebelum insert ulang", deleted);
        }

        List<DepositFdEntity> batch = new ArrayList<>(BATCH_SIZE);
        int lineNo = 1;
        int inserted = 0;
        int skipped = 0;

        // ISO_8859_1 menerima semua byte 0-255 sehingga tidak pernah gagal karena
        // encoding, dan hasilnya konsisten di Windows maupun Linux.
        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.ISO_8859_1)) {
            reader.readLine(); // baris 1 selalu header dari EDW

            String line;
            while ((line = reader.readLine()) != null) {
                lineNo++;

                if (line.isBlank()) {
                    continue;
                }

                DepositFdEntity entity = parseLine(line, processDate, lineNo);
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

        log.info("[FD-SFTP] Selesai: {} baris dibaca, {} tersimpan, {} dilewati",
                lineNo, inserted, skipped);

        if (inserted == 0 && lineNo > 1) {
            log.error("[FD-SFTP] {} baris dibaca tapi tidak ada yang tersimpan. "
                    + "Cek format file dan log warning di atas.", lineNo);
        }

        return inserted;
    }

    private DepositFdEntity parseLine(String line, LocalDateTime processDate, int lineNo) {
        // -1 agar field kosong di akhir tetap dihitung
        String[] f = line.split("\\|", -1);

        if (f.length < EXPECTED_FIELDS) {
            if (lineNo <= 3) {
                log.error("[FD-SFTP] Baris {} hanya terpecah jadi {} kolom. Isi: [{}]",
                        lineNo, f.length, line);
            } else {
                log.warn("[FD-SFTP] Baris {} hanya punya {} kolom, dilewati", lineNo, f.length);
            }
            return null;
        }

        String cifNo = f[0].trim();
        String depositNumber = f[1].trim();

        if (cifNo.isEmpty() || depositNumber.isEmpty()) {
            log.warn("[FD-SFTP] Baris {} tanpa CIF atau nomor deposito, dilewati", lineNo);
            return null;
        }

        String currency = f[4].trim().toUpperCase();
        if (currency.length() > MAX_CURRENCY_LENGTH) {
            log.warn("[FD-SFTP] Baris {}: currency [{}] tidak valid, dilewati. Isi: [{}]",
                    lineNo, currency, line);
            return null;
        }

        DepositFdEntity e = new DepositFdEntity();
        e.setCifNo(cifNo);
        e.setDepositNumber(depositNumber);
        e.setPlacementDate(parseDate(f[2], lineNo));
        e.setMaturityDate(parseDate(f[3], lineNo));
        e.setCurrency(currency.isEmpty() ? null : currency);
        e.setPrincipal(parseDecimal(f[5], lineNo));
        e.setTenor(parseInt(f[6], lineNo));
        e.setInterestRate(parseDecimal(f[7], lineNo));
        e.setProcessDate(processDate);
        e.setCreatedDate(LocalDateTime.now());
        return e;
    }

    /** 00010101 adalah placeholder null di file EDW, bukan tanggal sungguhan. */
    private LocalDate parseDate(String value, int lineNo) {
        String v = (value == null) ? "" : value.trim();
        if (v.isEmpty() || NULL_DATE.equals(v)) {
            return null;
        }
        try {
            return LocalDate.parse(v, FILE_DATE);
        } catch (DateTimeParseException ex) {
            log.warn("[FD-SFTP] Baris {}: format tanggal tidak valid [{}]", lineNo, v);
            return null;
        }
    }

    private BigDecimal parseDecimal(String value, int lineNo) {
        String v = (value == null) ? "" : value.trim();
        if (v.isEmpty()) {
            return BigDecimal.ZERO;
        }
        try {
            return new BigDecimal(v);
        } catch (NumberFormatException ex) {
            log.warn("[FD-SFTP] Baris {}: format angka tidak valid [{}]", lineNo, v);
            return BigDecimal.ZERO;
        }
    }

    private Integer parseInt(String value, int lineNo) {
        String v = (value == null) ? "" : value.trim();
        if (v.isEmpty()) {
            return null;
        }
        try {
            return Integer.valueOf(v);
        } catch (NumberFormatException ex) {
            log.warn("[FD-SFTP] Baris {}: tenor tidak valid [{}]", lineNo, v);
            return null;
        }
    }
}