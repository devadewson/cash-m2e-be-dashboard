package com.m2e.dashboard.service.overview.AssetGraph;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

@Slf4j
@Service
public class GraphCurrencyConverter {

    private static final int SCALE = 2;
    private static final RoundingMode ROUNDING = RoundingMode.HALF_UP;
    private static final String IDR = "IDR";

    /** Konversi ke IDR  */
    public BigDecimal toIdr(BigDecimal amount, String fromCurrency,
                            Map<String, BigDecimal> rateMap) {
        if (amount == null || amount.signum() == 0) {
            return BigDecimal.ZERO;
        }
        if (fromCurrency == null || IDR.equalsIgnoreCase(fromCurrency)) {
            return amount.setScale(SCALE, ROUNDING);
        }

        BigDecimal rate = rateMap.get(fromCurrency.toUpperCase() + "." + IDR);
        if (rate == null || rate.signum() == 0) {
            log.warn("[GRAPH] Rate {}.IDR not found", fromCurrency);
            return null;
        }
        return amount.multiply(rate).setScale(SCALE, ROUNDING);
    }

    /** Konversi IDR ke currency target  */
    public BigDecimal fromIdr(BigDecimal amountIdr, String targetCurrency,
                              Map<String, BigDecimal> rateMap) {
        if (amountIdr == null || amountIdr.signum() == 0) {
            return BigDecimal.ZERO;
        }
        if (targetCurrency == null || IDR.equalsIgnoreCase(targetCurrency)) {
            return amountIdr.setScale(SCALE, ROUNDING);
        }

        String target = targetCurrency.toUpperCase();

        BigDecimal direct = rateMap.get(IDR + "." + target);
        if (direct != null && direct.signum() != 0) {
            return amountIdr.multiply(direct).setScale(SCALE, ROUNDING);
        }

        BigDecimal reverse = rateMap.get(target + "." + IDR);
        if (reverse == null || reverse.signum() == 0) {
            log.warn("[GRAPH] No rate for IDR->{}", target);
            return null;
        }
        return amountIdr.divide(reverse, SCALE, ROUNDING);
    }
}
