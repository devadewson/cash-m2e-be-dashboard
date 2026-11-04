package com.m2e.dashboard.service.trade.Trademart;

import com.m2e.dashboard.model.DataMart.DatamartAgreement;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;


@Component
@Slf4j
public class DataMapper {

    /** RELEASE_DATE, contoh 2025-09-30 19:50:34 */
    private static final DateTimeFormatter DATETIME_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /** RPT_DT dan MATURITY_DATE, contoh 20260709 */
    private static final DateTimeFormatter DATE_ONLY_FMT = DateTimeFormatter.ofPattern("yyyyMMdd");

    private static final int EXPECTED_FIELDS = 30;

    /**
     * @param lines        baris data (tanpa header)
     * @param processDate  sama untuk seluruh baris dalam satu kali eksekusi job
     * @param firstLineNo  nomor baris di file untuk elemen pertama list, untuk log
     */
    public List<DatamartAgreement> map(List<String> lines, LocalDateTime processDate, int firstLineNo) {
        List<DatamartAgreement> records = new ArrayList<>(lines.size());

        for (int idx = 0; idx < lines.size(); idx++) {
            String line = lines.get(idx);
            int lineNo = firstLineNo + idx;
            try {
                DatamartAgreement record = parse(line, processDate, lineNo);
                if (record != null) {
                    records.add(record);
                }
            } catch (Exception e) {
                log.warn("[TRADE-SFTP] Baris {} gagal di-parse: {} | isi: {}", lineNo, e.getMessage(), line);
            }
        }
        return records;
    }

    private DatamartAgreement parse(String line, LocalDateTime processDate, int lineNo) {
        // -1 agar kolom kosong di akhir tetap dihitung
        String[] f = line.split("\\|", -1);

        if (f.length < EXPECTED_FIELDS) {
            log.warn("[TRADE-SFTP] Baris {} punya {} kolom (harus {}), dilewati: {}",
                    lineNo, f.length, EXPECTED_FIELDS, line);
            return null;
        }

        DatamartAgreement g = new DatamartAgreement();

        g.setRptDt(dateOnly(f[0], lineNo));
        g.setMasterCode(trim(f[1]));
        g.setSeqCode(trim(f[2]));
        g.setSeqSrl(integer(f[3], lineNo));
        g.setPaymentOption(trim(f[4]));
        g.setProductType(trim(f[5]));
        g.setRefNo(trim(f[6]));
        g.setSeqNo(trim(f[7]));
        g.setCurrency(trim(f[8]));
        g.setInitialAmount(decimal(f[9], lineNo));
        g.setOutstandingAmount(decimal(f[10], lineNo));
        g.setReleaseDate(dateTime(f[11], lineNo));
        g.setMaturityDate(dateOnly(f[12], lineNo));
        g.setTenor(integer(f[13], lineNo));
        g.setCifDrawer(trim(f[14]));
        g.setDrawer(trim(f[15]));
        g.setCifDrawee(trim(f[16]));
        g.setDrawee(trim(f[17]));
        g.setCifRemitingBank(trim(f[18]));
        g.setRemitingBank(trim(f[19]));
        g.setCifApplicant(trim(f[20]));
        g.setApplicant(trim(f[21]));
        g.setCifBeneficiary(trim(f[22]));
        g.setBeneficiary(trim(f[23]));
        g.setCifIssuingBank(trim(f[24]));
        g.setIssuingBank(trim(f[25]));
        g.setCifDebitName(trim(f[26]));
        g.setDebitName(trim(f[27]));
        g.setFacility(trim(f[28]));
        g.setFacilityName(trim(f[29]));

        g.setProcessDate(processDate);
        return g;
    }

    private String trim(String v) {
        return v == null ? null : v.trim();
    }

    private BigDecimal decimal(String v, int lineNo) {
        if (v == null || v.isBlank()) {
            return BigDecimal.ZERO;
        }
        try {
            return new BigDecimal(v.replace(",", "").trim());
        } catch (Exception e) {
            log.warn("[TRADE-SFTP] Baris {}: angka tidak valid '{}'", lineNo, v);
            return BigDecimal.ZERO;
        }
    }

    private Integer integer(String v, int lineNo) {
        if (v == null || v.isBlank()) {
            return null;
        }
        try {
            return Integer.parseInt(v.trim());
        } catch (Exception e) {
            log.warn("[TRADE-SFTP] Baris {}: integer tidak valid '{}'", lineNo, v);
            return null;
        }
    }

    private LocalDateTime dateTime(String v, int lineNo) {
        if (v == null || v.isBlank()) {
            return null;
        }
        try {
            return LocalDateTime.parse(v.trim(), DATETIME_FMT);
        } catch (Exception e) {
            log.warn("[TRADE-SFTP] Baris {}: datetime tidak valid '{}'", lineNo, v);
            return null;
        }
    }

    private LocalDate dateOnly(String v, int lineNo) {
        if (v == null || v.isBlank()) {
            return null;
        }
        try {
            return LocalDate.parse(v.trim(), DATE_ONLY_FMT);
        } catch (Exception e) {
            log.warn("[TRADE-SFTP] Baris {}: tanggal tidak valid '{}'", lineNo, v);
            return null;
        }
    }
}
