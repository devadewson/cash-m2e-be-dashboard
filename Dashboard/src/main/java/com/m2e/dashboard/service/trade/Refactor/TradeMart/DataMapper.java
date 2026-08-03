package com.m2e.dashboard.service.trade.Refactor.TradeMart;

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

    // RELEASE_DATE format: 2025-09-30 19:50:34
    private static final DateTimeFormatter DATETIME_FMT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // RPT_DT / MATURITY_DATE format: 20260709
    private static final DateTimeFormatter DATE_ONLY_FMT =
            DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final int EXPECTED_FIELDS = 30;

    public List<DatamartAgreement> map(List<String> lines) {
        List<DatamartAgreement> records = new ArrayList<>(lines.size());

        for (int idx = 0; idx < lines.size(); idx++) {
            String line = lines.get(idx);
            try {
                DatamartAgreement record = parse(line);
                if (record != null) {
                    records.add(record);
                }
            } catch (Exception e) {
                log.warn("Parse error at line {}: {} | content: {}", idx, e.getMessage(), line);
            }
        }

        return records;
    }

    private DatamartAgreement parse(String line) {
        String[] f = line.split("\\|", -1);

        if (f.length < EXPECTED_FIELDS) {
            log.warn("Skipping malformed line, expected {} fields but got {}: {}",
                    EXPECTED_FIELDS, f.length, line);
            return null;
        }

        DatamartAgreement g = new DatamartAgreement();

        g.setRptDt(dateOnly(f[0]));
        g.setMasterCode(trim(f[1]));
        g.setSeqCode(trim(f[2]));
        g.setSeqSrl(i(f[3]));
        g.setPaymentOption(trim(f[4]));
        g.setProductType(trim(f[5]));
        g.setRefNo(trim(f[6]));
        g.setSeqNo(trim(f[7]));
        g.setCurrency(trim(f[8]));
        g.setInitialAmount(bd(f[9]));
        g.setOutstandingAmount(bd(f[10]));
        g.setReleaseDate(dt(f[11]));
        g.setMaturityDate(dateOnly(f[12]));
        g.setTenor(i(f[13]));
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

        g.setProcessDate(LocalDateTime.now());

        return g;
    }

    private String trim(String v) {
        return v == null ? null : v.trim();
    }

    private BigDecimal bd(String v) {
        try {
            if (v == null || v.isBlank()) {
                return BigDecimal.ZERO;
            }
            return new BigDecimal(v.replace(",", "").trim());
        } catch (Exception e) {
            log.warn("Invalid numeric value: '{}'", v);
            return BigDecimal.ZERO;
        }
    }

    private Integer i(String v) {
        try {
            return (v != null && !v.isBlank())
                    ? Integer.parseInt(v.trim())
                    : null;
        } catch (Exception e) {
            log.warn("Invalid integer value: '{}'", v);
            return null;
        }
    }

    private LocalDateTime dt(String v) {
        try {
            return (v != null && !v.isBlank())
                    ? LocalDateTime.parse(v.trim(), DATETIME_FMT)
                    : null;
        } catch (Exception e) {
            log.warn("Invalid datetime value: '{}'", v);
            return null;
        }
    }

    private LocalDate dateOnly(String v) {
        try {
            return (v != null && !v.isBlank())
                    ? LocalDate.parse(v.trim(), DATE_ONLY_FMT)
                    : null;
        } catch (Exception e) {
            log.warn("Invalid date value: '{}'", v);
            return null;
        }
    }
}