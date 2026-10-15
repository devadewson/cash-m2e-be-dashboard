package com.m2e.dashboard.service.loan;


import com.m2e.dashboard.config.CorpCifResolver;
import com.m2e.dashboard.config.UserAndCorp;
import com.m2e.dashboard.dto.common.ApiResponse;
import com.m2e.dashboard.dto.common.ErrorResponse;
import com.m2e.dashboard.dto.loan.LoanPbAmountByCurrencyProjection;
import com.m2e.dashboard.dto.loan.tlf.LoanPbAcctStatsDTO;
import com.m2e.dashboard.dto.overview.Refactor.KursResolution;
import com.m2e.dashboard.enums.LoanPbProductEnum;
import com.m2e.dashboard.repository.LoanPbDrawRepository;
import com.m2e.dashboard.repository.LoanPbFacilityRepository;
import com.m2e.dashboard.service.loan.jobDataMart.LoanPbCacheNames;
import com.m2e.dashboard.service.loan.jobDataMart.LoanPbDataDateResolver;
import com.m2e.dashboard.service.overview.AssetGraph.GraphCurrencyConverter;
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
 * Stats PB/PPB = ringkasan dari account list, di seluruh halaman.
 *
 * Initial limit      : total PLAFOND fasilitas yang tampil di account list
 * Outstanding balance: total OUTSTANDING draw yang tampil di account list
 * Keduanya dikonversi ke baseCurrency.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LoanPbStatsService {

    private final LoanPbFacilityRepository facilityRepository;
    private final LoanPbDrawRepository drawRepository;
    private final LoanPbDataDateResolver dataDateResolver;
    private final CorpCifResolver corpCifResolver;
    private final KursResolver kursResolver;
    private final GraphCurrencyConverter graphConverter;

    private static final String IDR = "IDR";
    private static final String CODE_SUCCESS = "0";
    private static final String CODE_FAILED = "1";
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DATETIME_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Cacheable(
            value = LoanPbCacheNames.STATS,
            key = "#user.corpId + '_' + #product.name() + '_' "
                    + "+ (#baseCurrency != null and !#baseCurrency.isBlank() "
                    + "   ? #baseCurrency.trim().toUpperCase() : 'IDR') + '_' "
                    + "+ T(java.time.LocalDate).now()",
            unless = "#result == null || #result.msgBody == null"
    )
    public ApiResponse<LoanPbAcctStatsDTO> getStats(UserAndCorp user, LoanPbProductEnum product,
                                                    String baseCurrency) {

        String target = (baseCurrency != null && !baseCurrency.isBlank())
                ? baseCurrency.trim().toUpperCase() : IDR;
        String accrueMethod = product.getAccrueMethod();

        String cifNo;
        try {
            cifNo = corpCifResolver.resolveCif(user.getCorpId());
        } catch (Exception e) {
            log.error("[{}-Stats] CIF gagal di-resolve corpId={}: {}",
                    product, user.getCorpId(), e.getMessage());
            return failure("CIF tidak ditemukan untuk korporasi ini");
        }

        try {
            Optional<LoanPbDataDateResolver.DataRange> range = dataDateResolver.resolveLatest();
            if (range.isEmpty()) {
                log.warn("[{}-Stats] Belum ada data PB/PPB di database", product);
                return success(buildDto(0, BigDecimal.ZERO, BigDecimal.ZERO, target, null));
            }
            LoanPbDataDateResolver.DataRange r = range.get();

            log.info("[{}-Stats] START cif={} currency={} dataDate={}",
                    product, cifNo, target, r.startOfDay().toLocalDate());

            List<LoanPbAmountByCurrencyProjection> limits = facilityRepository.sumPlafondByCurrency(
                    cifNo, accrueMethod, r.startOfDay(), r.endOfDay());

            if (limits.isEmpty()) {
                log.info("[{}-Stats] cif={} tidak punya fasilitas produk ini", product, cifNo);
                return success(buildDto(0, BigDecimal.ZERO, BigDecimal.ZERO, target, r.loadedAt()));
            }

            List<LoanPbAmountByCurrencyProjection> outstandings = drawRepository.sumOutstandingByCurrency(
                    cifNo, accrueMethod, r.startOfDay(), r.endOfDay());

            Map<String, BigDecimal> rateMap = Map.of();
            if (needsConversion(limits, target) || needsConversion(outstandings, target)) {
                KursResolution kurs = kursResolver.resolve();
                if (kurs == null) {
                    log.error("[{}-Stats] Kurs tidak tersedia dari Trapi maupun DB, cif={}", product, cifNo);
                    return failure("Kurs tidak tersedia untuk konversi ke " + target);
                }
                rateMap = kurs.getRateMap();
                log.info("[{}-Stats] Kurs source={} pairs={} age={}h",
                        product, kurs.getSource(), rateMap.size(), kurs.getAgeHours());
            }

            BigDecimal totalLimit = sumConverted(limits, target, rateMap);
            BigDecimal totalOutstanding = sumConverted(outstandings, target, rateMap);
            int facilities = limits.stream()
                    .mapToInt(g -> g.getCount() != null ? g.getCount().intValue() : 0)
                    .sum();

            log.info("[{}-Stats] SUCCESS cif={} facilities={} limit={} outstanding={} {}",
                    product, cifNo, facilities, totalLimit, totalOutstanding, target);

            return success(buildDto(facilities, totalLimit, totalOutstanding, target, r.loadedAt()));

        } catch (MissingRateException e) {
            log.error("[{}-Stats] {} | cif={}", product, e.getMessage(), cifNo);
            return failure(e.getMessage());
        } catch (Exception e) {
            log.error("[{}-Stats] FAILED cif={}: {}", product, cifNo, e.getMessage(), e);
            return failure("Data tidak dapat diproses saat ini");
        }
    }

    private boolean needsConversion(List<LoanPbAmountByCurrencyProjection> groups, String target) {
        return groups.stream().anyMatch(g -> g.getCurrency() == null
                || !target.equalsIgnoreCase(g.getCurrency().trim()));
    }

    /** Satu currency gagal dikonversi membatalkan seluruh stats. */
    private BigDecimal sumConverted(List<LoanPbAmountByCurrencyProjection> groups, String target,
                                    Map<String, BigDecimal> rateMap) {
        BigDecimal total = BigDecimal.ZERO;
        for (LoanPbAmountByCurrencyProjection g : groups) {
            BigDecimal amount = (g.getTotal() != null) ? g.getTotal() : BigDecimal.ZERO;
            BigDecimal converted = graphConverter.convert(amount, g.getCurrency(), target, rateMap);
            if (converted == null) {
                throw new MissingRateException(g.getCurrency(), target);
            }
            total = total.add(converted);
        }
        return total;
    }

    private LoanPbAcctStatsDTO buildDto(int facilities, BigDecimal limit, BigDecimal outstanding,
                                        String currency, LocalDateTime loadedAt) {
        LoanPbAcctStatsDTO dto = new LoanPbAcctStatsDTO();
        dto.setNumberOfFacilities(facilities);
        dto.setConsolidatedInitialLimit(format(limit));
        dto.setConsolidatedOutstandingBalance(format(outstanding));
        dto.setCurrency(currency);
        dto.setLastUpdatedDate(loadedAt != null ? loadedAt.format(DATE_FMT) : null);
        dto.setLastUpdatedDateTime(loadedAt != null ? loadedAt.format(DATETIME_FMT) : null);
        return dto;
    }

    private String format(BigDecimal value) {
        DecimalFormat formatter = new DecimalFormat("#,##0.00",
                DecimalFormatSymbols.getInstance(Locale.US));
        return formatter.format(value.setScale(2, RoundingMode.HALF_UP));
    }

    private ApiResponse<LoanPbAcctStatsDTO> success(LoanPbAcctStatsDTO body) {
        ApiResponse<LoanPbAcctStatsDTO> res = new ApiResponse<>();
        res.setMsgHeader(new ErrorResponse(CODE_SUCCESS));
        res.setMsgBody(body);
        return res;
    }

    private ApiResponse<LoanPbAcctStatsDTO> failure(String message) {
        ErrorResponse header = new ErrorResponse(CODE_FAILED);

        ApiResponse<LoanPbAcctStatsDTO> res = new ApiResponse<>();
        res.setMsgHeader(header);
        res.setMsgBody(null);
        return res;
    }

    private static final class MissingRateException extends RuntimeException {
        MissingRateException(String from, String to) {
            super("Kurs " + (from != null ? from : "(currency kosong)") + " ke " + to + " tidak ditemukan");
        }
    }
}
