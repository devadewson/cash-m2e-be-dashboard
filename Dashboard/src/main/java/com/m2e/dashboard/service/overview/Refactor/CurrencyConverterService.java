package com.m2e.dashboard.service.overview.Refactor;

import com.m2e.dashboard.model.Trapi.Response.KursRateDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service untuk konversi mata uang menggunakan data kurs dari Redis.
 *
 * Features:
 * - Build indexed rate map untuk O(1) lookup
 * - Konversi single account atau bulk
 * - Null safety & graceful degradation
 * - Support multiple clientname dengan prioritas
 */
@Slf4j
@Service
public class CurrencyConverterService {

    private static final int DECIMAL_SCALE = 6;
    private static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_UP;
    private static final String DEFAULT_CLIENT = "M2U_270";
    private static final BigDecimal ONE = BigDecimal.ONE;
    private static final BigDecimal ZERO = BigDecimal.ZERO;

    /**
     * Build indexed rate map dari data kurs untuk O(1) lookup.
     *
     * @param kursRates List data kurs dari API Trapi / Redis Kurs
     * @return Map dengan key "{FROM}.{TO}" dan value rate bidAllIn
     */
    public Map<String, BigDecimal> buildRateMap(List<KursRateDTO> kursRates) {
        Map<String, BigDecimal> rateMap = new HashMap<>();

        if (kursRates == null || kursRates.isEmpty()) {
            return rateMap;
        }

        // Single pass - NO groupingBy
        for (KursRateDTO rate : kursRates) {
            // Skip invalid
            if (!"SPOT".equalsIgnoreCase(rate.getTenor())) continue;
            if (!"true".equalsIgnoreCase(rate.getBidState())) continue;
            if (!"true".equalsIgnoreCase(rate.getAskState())) continue;
            if (rate.getBidAllIn() == null || rate.getBidAllIn().trim().isEmpty()) continue;
            if (rate.getCcy() == null) continue;

            String pairKey = rate.getCcy();

            // Skip if already have M2U_270
            if (rateMap.containsKey(pairKey) &&
                    "M2U_270".equals(rate.getClientName())) {
                // Override with M2U_270 (higher priority)
                rateMap.put(pairKey, new BigDecimal(rate.getBidAllIn()));
            } else if (!rateMap.containsKey(pairKey)) {
                // First occurrence
                rateMap.put(pairKey, new BigDecimal(rate.getBidAllIn()));
            }
        }

        // Add defaults
        addDefaultSameCurrencyRates(rateMap);

        return rateMap;
    }

    /**
     * Konversi single amount dari satu mata uang ke mata uang target.
     *
     * @param amount       Jumlah dalam mata uang asli
     * @param fromCurrency Kode mata uang asal (e.g., "CNY")
     * @param toCurrency   Kode mata uang target (e.g., "USD")
     * @param rateMap      Map kurs yang sudah di-build
     * @return Converted amount dalam mata uang target
     */
    public BigDecimal convert(BigDecimal amount, String fromCurrency, String toCurrency,
                              Map<String, BigDecimal> rateMap) {
        // Validate input
        if (amount == null || amount.compareTo(ZERO) <= 0) {
            log.debug("Amount is null or zero/negative: {}, skipping conversion", amount);
            return ZERO;
        }

        if (fromCurrency == null || toCurrency == null) {
            log.warn("Currency is null: from={}, to={}", fromCurrency, toCurrency);
            return ZERO;
        }

        // Same currency - no conversion needed
        if (fromCurrency.equalsIgnoreCase(toCurrency)) {
            log.debug("Same currency: {} = {}, no conversion needed", fromCurrency, toCurrency);
            return amount.setScale(DECIMAL_SCALE, ROUNDING_MODE);
        }

        // Build pair key
        String pairKey = fromCurrency.toUpperCase() + "." + toCurrency.toUpperCase();

        // Find exchange rate
        BigDecimal rate = rateMap.get(pairKey);

        if (rate == null) {
            log.warn("Exchange rate not found for pair: {}. " +
                            "Amount {} {} will be set to 0 {}",
                    pairKey, amount, fromCurrency, toCurrency);
            return ZERO;
        }

        // Calculate converted amount
        BigDecimal convertedAmount = amount.multiply(rate).setScale(DECIMAL_SCALE, ROUNDING_MODE);

        log.debug("Converted: {} {} × {} = {} {}",
                amount, fromCurrency, rate, convertedAmount, toCurrency);

        return convertedAmount;
    }

    /**
     * Find exchange rate untuk pair tertentu tanpa kalkulasi.
     *
     * @param fromCurrency Kode mata uang asal
     * @param toCurrency   Kode mata uang target
     * @param rateMap      Map kurs yang sudah di-build
     * @return Optional rate jika ditemukan
     */
    public Optional<BigDecimal> findExchangeRate(String fromCurrency, String toCurrency,
                                                 Map<String, BigDecimal> rateMap) {
        if (fromCurrency == null || toCurrency == null) {
            return Optional.empty();
        }

        if (fromCurrency.equalsIgnoreCase(toCurrency)) {
            return Optional.of(ONE);
        }

        String pairKey = fromCurrency.toUpperCase() + "." + toCurrency.toUpperCase();
        return Optional.ofNullable(rateMap.get(pairKey));
    }

    // ==================== PRIVATE METHODS ====================

    /**
     * Filter kurs yang valid untuk konversi.
     */
    private boolean isValidRate(KursRateDTO rate) {
        // Harus SPOT (bukan TODAY)
        if (!"SPOT".equalsIgnoreCase(rate.getTenor())) {
            return false;
        }

        // Bid state harus active
        if (!"true".equalsIgnoreCase(rate.getBidState())) {
            return false;
        }

        // Ask state harus active (optional, lebih strict)
        if (!"true".equalsIgnoreCase(rate.getAskState())) {
            return false;
        }

        // bidAllIn tidak boleh null
        if (rate.getBidAllIn() == null || rate.getBidAllIn().trim().isEmpty()) {
            return false;
        }

        return true;
    }

    /**
     * Pilih rate terbaik dari multiple client.
     * Prioritas: M2U_270 > first available
     */
    private BigDecimal selectBestRate(List<KursRateDTO> rates) {
        if (rates == null || rates.isEmpty()) {
            return null;
        }

        // Cari M2U_270 dulu
        Optional<KursRateDTO> preferred = rates.stream()
                .filter(r -> DEFAULT_CLIENT.equalsIgnoreCase(r.getClientName()))
                .findFirst();

        KursRateDTO selected = preferred.orElse(rates.get(0));

        try {
            return new BigDecimal(selected.getBidAllIn());
        } catch (NumberFormatException e) {
            log.warn("Invalid rate format for pair {}: {}", selected.getCcy(), selected.getBidAllIn());
            return null;
        }
    }

    /**
     * Tambah default rate = 1 untuk same currency pairs.
     */
    private void addDefaultSameCurrencyRates(Map<String, BigDecimal> rateMap) {
        // Common currencies
        String[] currencies = {"USD", "IDR", "EUR", "GBP", "JPY", "CNY", "SGD", "AUD",
                "CAD", "CHF", "HKD", "NZD", "KRW", "MYR"};

        for (String currency : currencies) {
            String pairKey = currency + "." + currency;
            rateMap.putIfAbsent(pairKey, ONE);
        }
    }
}