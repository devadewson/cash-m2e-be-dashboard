package com.m2e.dashboard.service.overview.Refactor;

import com.m2e.dashboard.model.Trapi.Response.KursRateDTO;
import com.m2e.dashboard.service.overview.AssetGraph.GraphCurrencyConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Membangun rate map dari data Trapi.
 * Konversi didelegasikan ke GraphCurrencyConverter.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CurrencyConverterService {

    private final GraphCurrencyConverter graphConverter;

    /** Prioritas client. Angka kecil menang. Client di luar daftar dapat prioritas terendah. */
    private static final Map<String, Integer> CLIENT_PRIORITY = Map.of(
            "M2U_270", 1,
            "M2U_EM", 2,
            "M2U_PG", 3,
            "M2U_PW", 4,
            "GMS-TRAPI_270", 5);

    /**
     * Build rate map dari data kurs. Key "{FROM}.{TO}", value bidAllIn.
     * Hanya SPOT dengan bidState dan askState aktif.
     */
    public Map<String, BigDecimal> buildRateMap(List<KursRateDTO> kursRates) {
        Map<String, BigDecimal> rateMap = new HashMap<>();
        Map<String, Integer> bestPriority = new HashMap<>();

        if (kursRates == null || kursRates.isEmpty()) {
            log.warn("Kurs list kosong, rate map kosong");
            return rateMap;
        }

        int skipped = 0;

        for (KursRateDTO rate : kursRates) {
            if (!isValidRate(rate)) {
                skipped++;
                continue;
            }

            String pairKey = rate.getCcy().trim().toUpperCase();
            int priority = CLIENT_PRIORITY.getOrDefault(rate.getClientName(), Integer.MAX_VALUE);

            Integer current = bestPriority.get(pairKey);
            if (current != null && priority >= current) {
                continue;
            }

            BigDecimal value = parseRate(rate.getBidAllIn(), pairKey);
            if (value == null) {
                continue;
            }

            rateMap.put(pairKey, value);
            bestPriority.put(pairKey, priority);
        }

        log.info("Rate map built: {} pairs ({} baris di-skip)", rateMap.size(), skipped);
        return rateMap;
    }

    /**
     * @deprecated Pakai GraphCurrencyConverter.convert langsung.
     * Mengembalikan null kalau rate tidak ditemukan, bukan ZERO.
     */
    @Deprecated
    public BigDecimal convert(BigDecimal amount, String fromCurrency, String toCurrency,
                              Map<String, BigDecimal> rateMap) {
        return graphConverter.convert(amount, fromCurrency, toCurrency, rateMap);
    }

    public Optional<BigDecimal> findExchangeRate(String fromCurrency, String toCurrency,
                                                 Map<String, BigDecimal> rateMap) {
        if (fromCurrency == null || toCurrency == null || rateMap == null) {
            return Optional.empty();
        }
        if (fromCurrency.equalsIgnoreCase(toCurrency)) {
            return Optional.of(BigDecimal.ONE);
        }
        String pairKey = fromCurrency.trim().toUpperCase() + "." + toCurrency.trim().toUpperCase();
        return Optional.ofNullable(rateMap.get(pairKey));
    }

    private boolean isValidRate(KursRateDTO rate) {
        if (rate == null) return false;
        if (rate.getCcy() == null || rate.getCcy().isBlank()) return false;
        if (!"SPOT".equalsIgnoreCase(rate.getTenor())) return false;
        if (!"true".equalsIgnoreCase(rate.getBidState())) return false;
        if (!"true".equalsIgnoreCase(rate.getAskState())) return false;
        return rate.getBidAllIn() != null && !rate.getBidAllIn().isBlank();
    }

    private BigDecimal parseRate(String value, String pairKey) {
        try {
            return new BigDecimal(value.trim());
        } catch (NumberFormatException e) {
            log.warn("Format rate tidak valid untuk {}: [{}]", pairKey, value);
            return null;
        }
    }
}