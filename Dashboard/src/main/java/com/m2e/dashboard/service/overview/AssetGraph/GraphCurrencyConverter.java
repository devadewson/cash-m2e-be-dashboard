package com.m2e.dashboard.service.overview.AssetGraph;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class GraphCurrencyConverter {

    /** Scale hasil akhir. */
    private static final int SCALE = 2;
    /** Scale perhitungan internal, tinggi agar triangulasi tidak kehilangan presisi. */
    private static final int CALC_SCALE = 12;
    private static final RoundingMode ROUNDING = RoundingMode.HALF_UP;
    private static final String IDR = "IDR";

    private static final List<String> BRIDGES = List.of("IDR", "USD");

    /**
     * Konversi antar mata uang apa pun.
     * Urutan: pair langsung, pair kebalik, lalu triangulasi lewat IDR atau USD.
     * Null kalau rate tidak ditemukan sama sekali.
     */
    public BigDecimal convert(BigDecimal amount, String fromCurrency, String toCurrency,
                              Map<String, BigDecimal> rateMap) {
        if (amount == null || amount.signum() == 0) {
            return BigDecimal.ZERO;
        }
        if (fromCurrency == null || toCurrency == null || rateMap == null) {
            return null;
        }

        String from = fromCurrency.trim().toUpperCase();
        String to = toCurrency.trim().toUpperCase();

        if (from.equals(to)) {
            return amount.setScale(SCALE, ROUNDING);
        }

        BigDecimal direct = convertRaw(amount, from, to, rateMap);
        if (direct != null) {
            return direct.setScale(SCALE, ROUNDING);
        }

        for (String bridge : BRIDGES) {
            if (bridge.equals(from) || bridge.equals(to)) {
                continue;
            }
            BigDecimal mid = convertRaw(amount, from, bridge, rateMap);
            if (mid == null) {
                continue;
            }
            BigDecimal result = convertRaw(mid, bridge, to, rateMap);
            if (result != null) {
                log.debug("Triangulasi {} -> {} -> {}", from, bridge, to);
                return result.setScale(SCALE, ROUNDING);
            }
        }

        log.warn("Rate tidak ditemukan untuk {} -> {}", from, to);
        return null;
    }

    /** Pair langsung atau kebalikannya, hasil scale tinggi tanpa pembulatan akhir. */
    private BigDecimal convertRaw(BigDecimal amount, String from, String to,
                                  Map<String, BigDecimal> rateMap) {
        BigDecimal rate = rateMap.get(from + "." + to);
        if (rate != null && rate.signum() != 0) {
            return amount.multiply(rate);
        }

        BigDecimal reverse = rateMap.get(to + "." + from);
        if (reverse != null && reverse.signum() != 0) {
            return amount.divide(reverse, CALC_SCALE, ROUNDING);
        }
        return null;
    }

    /** Konversi ke IDR, untuk jalur tulis job snapshot. */
    public BigDecimal toIdr(BigDecimal amount, String fromCurrency,
                            Map<String, BigDecimal> rateMap) {
        return convert(amount, fromCurrency, IDR, rateMap);
    }

    /** Konversi dari IDR ke mata uang target, untuk jalur baca grafik. */
    public BigDecimal fromIdr(BigDecimal amountIdr, String targetCurrency,
                              Map<String, BigDecimal> rateMap) {
        return convert(amountIdr, IDR, targetCurrency, rateMap);
    }
}