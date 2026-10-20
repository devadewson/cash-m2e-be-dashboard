package com.m2e.dashboard.service.overview.esbRate;

import com.m2e.dashboard.model.esbRate.EsbExchangeRateResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Slf4j
@Component
public class EsbKursMapper {

    private static final String IDR = "IDR";
    private static final DateTimeFormatter VALUE_DATE = DateTimeFormatter.ofPattern("yyyyMMdd");

    public Map<String, BigDecimal> toRateMap(List<EsbExchangeRateResponse.ExchangeRateElement> elements) {
        Map<String, BigDecimal> rateMap = new HashMap<>();
        int skipped = 0;

        for (EsbExchangeRateResponse.ExchangeRateElement e : elements) {
            String ccy = (e.getCurrency() == null) ? "" : e.getCurrency().trim().toUpperCase();

            if (!isIsoCode(ccy) || IDR.equals(ccy)) {
                skipped++;
                continue;
            }

            BigDecimal rate = parseRate(e.getTtBuyRate());
            if (rate == null || rate.signum() <= 0) {
                skipped++;
                continue;
            }

            rateMap.put(ccy + "." + IDR, rate);
        }

        log.info("[KURS-ESB] Rate map: {} pairs ({} baris dilewati)", rateMap.size(), skipped);
        return rateMap;
    }

    public String toValueDate(String timeStamp) {
        try {
            if (timeStamp != null && timeStamp.length() >= 10) {
                return LocalDate.parse(timeStamp.substring(0, 10)).format(VALUE_DATE);
            }
        } catch (Exception ignored) {

        }
        return LocalDate.now().format(VALUE_DATE);
    }

    private boolean isIsoCode(String ccy) {
        return ccy.length() == 3 && ccy.chars().allMatch(ch -> ch >= 'A' && ch <= 'Z');
    }

    private BigDecimal parseRate(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        String v = value.trim();
        if (v.contains("-")) {
            return null;
        }
        try {
            return new BigDecimal(v.replace(",", ""));
        } catch (NumberFormatException ex) {
            log.warn("[KURS-ESB] Format rate tidak valid: [{}]", value);
            return null;
        }
    }
}
