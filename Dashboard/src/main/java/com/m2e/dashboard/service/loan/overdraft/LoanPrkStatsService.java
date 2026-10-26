package com.m2e.dashboard.service.loan.overdraft;

import com.m2e.dashboard.config.CorpCifResolver;
import com.m2e.dashboard.config.UserAndCorp;
import com.m2e.dashboard.dto.common.ApiResponse;
import com.m2e.dashboard.dto.common.ErrorResponse;
import com.m2e.dashboard.dto.loan.overdraft.LoanPrkStatsByCurrencyProjection;
import com.m2e.dashboard.dto.loan.overdraft.response.LNOverdraftAccountStatisticDTO;
import com.m2e.dashboard.dto.overview.Refactor.KursResolution;
import com.m2e.dashboard.repository.LoanPrkRepository;
import com.m2e.dashboard.service.overview.AssetGraph.GraphCurrencyConverter;
import com.m2e.dashboard.service.overview.Datamart.DatamartDateRange;
import com.m2e.dashboard.service.overview.Datamart.DatamartDateResolver;
import com.m2e.dashboard.service.overview.JobKursService.KursResolver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

/**
 * Statistik PRK dari DATAMART_LOAN_PRK, tanggal muat terakhir.
 *
 * OUTSTANDING berasal dari CURRBAL: saldo negatif berarti plafond terpakai.
 * Utilised amount = jumlah CURRBAL negatif dijadikan positif; saldo positif diabaikan.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LoanPrkStatsService {

    private final LoanPrkRepository repository;
    private final CorpCifResolver corpCifResolver;
    private final KursResolver kursResolver;
    private final GraphCurrencyConverter graphConverter;
    private final DatamartDateResolver dateResolver;

    private static final String IDR = "IDR";
    private static final String CODE_SUCCESS = "0";
    private static final String CODE_FAILED = "1";
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DATETIME_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Cacheable(
            value = "loanPrkStats",
            key = "#user.corpId + '_' "
                    + "+ (#baseCurrency != null and !#baseCurrency.isBlank() "
                    + "   ? #baseCurrency.trim().toUpperCase() : 'IDR') + '_' "
                    + "+ T(java.time.LocalDate).now()",
            unless = "#result == null || #result.msgBody == null"
    )
    public ApiResponse<LNOverdraftAccountStatisticDTO> getStats(UserAndCorp user, String baseCurrency) {

        String targetCurrency = (baseCurrency != null && !baseCurrency.isBlank())
                ? baseCurrency.trim().toUpperCase() : IDR;

        String cifNo;
        try {
            cifNo = corpCifResolver.resolveCif(user.getCorpId());
        } catch (Exception e) {
            log.error("[PRK-Stats] CIF gagal di-resolve corpId={}: {}", user.getCorpId(), e.getMessage());
            return failure("CIF tidak ditemukan untuk korporasi ini");
        }

        try {
            Optional<DatamartDateRange> range = dateResolver.prk();
            if (range.isEmpty()) {
                log.warn("[PRK-Stats] Belum ada data PRK di database");
                return success(buildDto(0L, BigDecimal.ZERO, BigDecimal.ZERO, targetCurrency, null));
            }
            DatamartDateRange r = range.get();

            log.info("[PRK-Stats] START cif={} currency={} dataDate={}", cifNo, targetCurrency, r.dataDate());

            List<LoanPrkStatsByCurrencyProjection> groups =
                    repository.getStatsByCurrency(cifNo, r.startOfDay(), r.endOfDay());

            if (groups.isEmpty()) {
                log.info("[PRK-Stats] Tidak ada PRK untuk cif={} dataDate={}", cifNo, r.dataDate());
                return success(buildDto(0L, BigDecimal.ZERO, BigDecimal.ZERO, targetCurrency, r.loadedAt()));
            }

            boolean needsConversion = groups.stream()
                    .anyMatch(g -> g.getCurrency() == null
                            || !targetCurrency.equalsIgnoreCase(g.getCurrency().trim()));

            Map<String, BigDecimal> rateMap = Map.of();
            if (needsConversion) {
                KursResolution kurs = kursResolver.resolve();
                if (kurs == null) {
                    log.error("[PRK-Stats] Kurs tidak tersedia, cif={}", cifNo);
                    return failure("Kurs tidak tersedia untuk konversi ke " + targetCurrency);
                }
                rateMap = kurs.getRateMap();
                log.info("[PRK-Stats] Kurs source={} pairs={} age={}h",
                        kurs.getSource(), rateMap.size(), kurs.getAgeHours());
            }

            long totalAccounts = 0L;
            BigDecimal totalLimit = BigDecimal.ZERO;
            BigDecimal totalUtilised = BigDecimal.ZERO;

            for (LoanPrkStatsByCurrencyProjection g : groups) {
                totalAccounts += (g.getNoOfAccounts() != null ? g.getNoOfAccounts() : 0L);

                BigDecimal limit = convert(g.getTotalLimit(), g.getCurrency(), targetCurrency, rateMap);
                BigDecimal utilised = convert(g.getTotalUtilised(), g.getCurrency(), targetCurrency, rateMap);

                if (limit == null || utilised == null) {
                    log.error("[PRK-Stats] Gagal konversi {} -> {} untuk cif={}. Total dibatalkan.",
                            g.getCurrency(), targetCurrency, cifNo);
                    return failure("Kurs " + g.getCurrency() + " ke " + targetCurrency + " tidak ditemukan");
                }

                totalLimit = totalLimit.add(limit);
                totalUtilised = totalUtilised.add(utilised);
            }

            log.info("[PRK-Stats] SUCCESS cif={} accounts={} limit={} utilised={} {}",
                    cifNo, totalAccounts, totalLimit, totalUtilised, targetCurrency);

            return success(buildDto(totalAccounts, totalLimit, totalUtilised, targetCurrency, r.loadedAt()));

        } catch (Exception e) {
            log.error("[PRK-Stats] FAILED cif={}: {}", cifNo, e.getMessage(), e);
            return failure("Data PRK tidak dapat diproses saat ini");
        }
    }

    private BigDecimal convert(BigDecimal amount, String from, String to, Map<String, BigDecimal> rateMap) {
        BigDecimal value = (amount != null) ? amount : BigDecimal.ZERO;
        return graphConverter.convert(value, from, to, rateMap);
    }

    /** lastUpdated mengikuti waktu muat data; kalau belum ada data, waktu sekarang. */
    private LNOverdraftAccountStatisticDTO buildDto(long count, BigDecimal limit, BigDecimal utilised,
                                                    String currency, LocalDateTime loadedAt) {
        LocalDateTime updatedAt = (loadedAt != null) ? loadedAt : LocalDateTime.now();
        LNOverdraftAccountStatisticDTO dto = new LNOverdraftAccountStatisticDTO();
        dto.setConsolidatedOverdraftLimitAmount(format(limit));
        dto.setConsolidatedUtilisedAmount(format(utilised));
        dto.setCurrency(currency);
        dto.setNumberOfAccounts((int) count);
        dto.setLastUpdatedDate(updatedAt.format(DATE_FMT));
        dto.setLastUpdatedDateTime(updatedAt.format(DATETIME_FMT));
        return dto;
    }

    /** DecimalFormat tidak thread-safe, jadi dibuat baru tiap pemanggilan. */
    private String format(BigDecimal value) {
        DecimalFormat formatter = new DecimalFormat("#,##0.00",
                DecimalFormatSymbols.getInstance(Locale.US));
        return formatter.format(value.setScale(2, RoundingMode.HALF_UP));
    }

    private ApiResponse<LNOverdraftAccountStatisticDTO> success(LNOverdraftAccountStatisticDTO body) {
        ApiResponse<LNOverdraftAccountStatisticDTO> res = new ApiResponse<>();
        res.setMsgHeader(new ErrorResponse(CODE_SUCCESS));
        res.setMsgBody(body);
        return res;
    }

    private ApiResponse<LNOverdraftAccountStatisticDTO> failure(String message) {
        ErrorResponse header = new ErrorResponse(CODE_FAILED);
        header.setErrorMessage(message);
        ApiResponse<LNOverdraftAccountStatisticDTO> res = new ApiResponse<>();
        res.setMsgHeader(header);
        res.setMsgBody(null);
        return res;
    }
}
