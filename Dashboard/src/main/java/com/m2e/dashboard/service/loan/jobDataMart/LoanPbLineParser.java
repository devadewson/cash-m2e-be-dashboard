package com.m2e.dashboard.service.loan.jobDataMart;

import com.m2e.dashboard.model.DataMart.LoanPbBaseEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.function.Supplier;

/**
 * Parser satu baris file PB/PPB, dipakai untuk file fasilitas maupun draw.
 *
 * Urutan kolom:
 * BASE_DT|CIF_NO|NOTE_NO|DRAW_NO|TGL_DRAW|OUTSTANDING|MATURITY_DATE|NEXT_PAYMENT_DUE|
 * NEXT_PAYMENT_DATE|PLAFOND|AVAILABLE_LIMIT|AVAILABLE_PERIOD|ACCRUE_METHOD|SUBLIMIT|STATUS|CURR_CD
 */
@Slf4j
@Component
public class LoanPbLineParser {

    private static final int EXPECTED_FIELDS = 16;
    private static final int MAX_CURRENCY_LENGTH = 3;
    private static final int RAW_DATE_LENGTH = 8;
    private static final DateTimeFormatter FILE_DATE = DateTimeFormatter.ofPattern("yyyyMMdd");

    /** Null kalau baris tidak valid. */
    public <T extends LoanPbBaseEntity> T parse(String line, Supplier<T> factory,
                                                LocalDateTime processDate, int lineNo, String tag) {
        // -1 agar field kosong di akhir tetap dihitung
        String[] f = line.split("\\|", -1);

        if (f.length < EXPECTED_FIELDS) {
            if (lineNo <= 3) {
                log.error("[{}] Baris {} hanya terpecah jadi {} kolom. Isi: [{}]",
                        tag, lineNo, f.length, line);
            } else {
                log.warn("[{}] Baris {} hanya punya {} kolom, dilewati", tag, lineNo, f.length);
            }
            return null;
        }

        String cifNo = f[1].trim();
        String noteNo = f[2].trim();
        if (cifNo.isEmpty() || noteNo.isEmpty()) {
            log.warn("[{}] Baris {} tanpa CIF atau NOTE_NO, dilewati", tag, lineNo);
            return null;
        }

        String currency = f[15].trim().toUpperCase();
        if (currency.length() > MAX_CURRENCY_LENGTH) {
            log.warn("[{}] Baris {}: currency [{}] tidak valid, dilewati", tag, lineNo, currency);
            return null;
        }

        T e = factory.get();
        e.setBaseDate(baseDate(f[0], lineNo, tag));
        e.setCifNo(cifNo);
        e.setNoteNo(noteNo);
        e.setDrawNo(text(f[3]));
        e.setDrawDate(rawDate(f[4], lineNo, tag));
        e.setOutstanding(decimal(f[5], lineNo, tag));
        e.setMaturityDate(rawDate(f[6], lineNo, tag));
        e.setNextPaymentDue(decimal(f[7], lineNo, tag));
        e.setNextPaymentDate(rawDate(f[8], lineNo, tag));
        e.setPlafond(decimal(f[9], lineNo, tag));
        e.setAvailableLimit(decimal(f[10], lineNo, tag));
        e.setAvailablePeriod(rawDate(f[11], lineNo, tag));
        e.setAccrueMethod(text(f[12]));
        e.setSublimit(text(f[13]));
        e.setStatus(text(f[14]));
        e.setCurrency(currency.isEmpty() ? null : currency);
        e.setProcessDate(processDate);
        e.setCreatedDate(LocalDateTime.now());
        return e;
    }

    private String text(String value) {
        String v = (value == null) ? "" : value.trim();
        return v.isEmpty() ? null : v;
    }

    /** Tanggal tampilan disimpan apa adanya; hanya ditolak kalau bukan 8 karakter. */
    private String rawDate(String value, int lineNo, String tag) {
        String v = text(value);
        if (v != null && v.length() != RAW_DATE_LENGTH) {
            log.warn("[{}] Baris {}: tanggal [{}] bukan 8 karakter, disimpan kosong", tag, lineNo, v);
            return null;
        }
        return v;
    }

    private LocalDate baseDate(String value, int lineNo, String tag) {
        String v = text(value);
        if (v == null) {
            return null;
        }
        try {
            return LocalDate.parse(v, FILE_DATE);
        } catch (DateTimeParseException ex) {
            log.warn("[{}] Baris {}: BASE_DT [{}] tidak valid", tag, lineNo, v);
            return null;
        }
    }

    /** Nilai punya spasi di depan dan bisa negatif. */
    private BigDecimal decimal(String value, int lineNo, String tag) {
        String v = text(value);
        if (v == null) {
            return BigDecimal.ZERO;
        }
        try {
            return new BigDecimal(v);
        } catch (NumberFormatException ex) {
            log.warn("[{}] Baris {}: angka [{}] tidak valid", tag, lineNo, v);
            return BigDecimal.ZERO;
        }
    }
}
