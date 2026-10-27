package com.m2e.dashboard.service.loan.plafond;

import com.m2e.dashboard.config.CorpCifResolver;
import com.m2e.dashboard.config.UserAndCorp;
import com.m2e.dashboard.dto.common.ApiResponse;
import com.m2e.dashboard.dto.common.ErrorResponse;
import com.m2e.dashboard.dto.loan.plafond.response.LoanPlafondStatsDTO;
import com.m2e.dashboard.dto.overview.Refactor.KursResolution;
import com.m2e.dashboard.repository.LoanPlafondRepository;
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
import java.util.Optional;

/**
 * Plafond per CIF dari DATAMART_PLAFOND, tanggal muat terakhir.
 * Sumber selalu IDR, dikonversi ke baseCurrency.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LoanPlafondStatsService {

    private final LoanPlafondRepository repository;
    private final CorpCifResolver corpCifResolver;
    private final DatamartDateResolver dateResolver;
    private final KursResolver kursResolver;
    private final GraphCurrencyConverter graphConverter;

    private static final String IDR = "IDR";
    private static final String CODE_SUCCESS = "0";
    private static final String CODE_FAILED = "1";
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DATETIME_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Cacheable(
            value = LoanPlafondCacheNames.STATS,
            key = "#user.corpId + '_' "
                    + "+ (#baseCurrency != null and !#baseCurrency.isBlank() "
                    + "   ? #baseCurrency.trim().toUpperCase() : 'IDR') + '_' "
                    + "+ T(java.time.LocalDate).now()",
            unless = "#result == null || #result.msgBody == null"
    )
    public ApiResponse<LoanPlafondStatsDTO> getStats(UserAndCorp user, String baseCurrency) {

        String targetCurrency = (baseCurrency != null && !baseCurrency.isBlank())
                ? baseCurrency.trim().toUpperCase() : IDR;

        String cifNo;
        try {
            cifNo = corpCifResolver.resolveCif(user.getCorpId());
        } catch (Exception e) {
            log.error("[PLAFOND-Stats] CIF gagal di-resolve corpId={}: {}", user.getCorpId(), e.getMessage());
            return failure("CIF tidak ditemukan untuk korporasi ini");
        }

        try {
            Optional<DatamartDateRange> range = dateResolver.plafond();
            if (range.isEmpty()) {
                log.warn("[PLAFOND-Stats] Belum ada data plafond di database");
                return success(buildDto(BigDecimal.ZERO, null));
            }
            DatamartDateRange r = range.get();

            log.info("[PLAFOND-Stats] START cif={} currency={} dataDate={}", cifNo, targetCurrency, r.dataDate());

            List<BigDecimal> rows = repository.findPlafonds(cifNo, r.startOfDay(), r.endOfDay());
            if (rows.isEmpty()) {
                log.info("[PLAFOND-Stats] CIF {} tidak ada di file plafond dataDate={}", cifNo, r.dataDate());
                return success(buildDto(BigDecimal.ZERO, r.loadedAt()));
            }

            BigDecimal plafondIdr = rows.get(0) != null ? rows.get(0) : BigDecimal.ZERO;

            // IDR atau nilai 0 tidak butuh kurs
            if (IDR.equals(targetCurrency) || plafondIdr.signum() == 0) {
                log.info("[PLAFOND-Stats] SUCCESS cif={} plafond={} {}", cifNo, plafondIdr, targetCurrency);
                return success(buildDto(plafondIdr, r.loadedAt()));
            }

            KursResolution kurs = kursResolver.resolve();
            if (kurs == null) {
                log.error("[PLAFOND-Stats] Kurs tidak tersedia, cif={}", cifNo);
                return failure("Kurs tidak tersedia untuk konversi ke " + targetCurrency);
            }

            BigDecimal converted = graphConverter.convert(plafondIdr, IDR, targetCurrency, kurs.getRateMap());
            if (converted == null) {
                log.error("[PLAFOND-Stats] Kurs IDR -> {} tidak ditemukan, cif={} source={}",
                        targetCurrency, cifNo, kurs.getSource());
                return failure("Kurs " + IDR + " ke " + targetCurrency + " tidak ditemukan");
            }

            log.info("[PLAFOND-Stats] SUCCESS cif={} plafondIdr={} -> {} {} (kurs {})",
                    cifNo, plafondIdr, converted, targetCurrency, kurs.getSource());
            return success(buildDto(converted, r.loadedAt()));

        } catch (Exception e) {
            log.error("[PLAFOND-Stats] FAILED cif={}: {}", cifNo, e.getMessage(), e);
            return failure("Data plafond tidak dapat diproses saat ini");
        }
    }

    /** lastUpdated = waktu data masuk DB; kalau belum pernah ada data, waktu sekarang. */
    private LoanPlafondStatsDTO buildDto(BigDecimal plafond, LocalDateTime loadedAt) {
        LocalDateTime updatedAt = (loadedAt != null) ? loadedAt : LocalDateTime.now();
        LoanPlafondStatsDTO dto = new LoanPlafondStatsDTO();
        dto.setPlafond(format(plafond));
        dto.setLastUpdatedDate(updatedAt.format(DATE_FMT));
        dto.setLastUpdatedDateTime(updatedAt.format(DATETIME_FMT));
        return dto;
    }

    private String format(BigDecimal value) {
        DecimalFormat formatter = new DecimalFormat("#,##0.00", DecimalFormatSymbols.getInstance(Locale.US));
        return formatter.format(value.setScale(2, RoundingMode.HALF_UP));
    }

    private ApiResponse<LoanPlafondStatsDTO> success(LoanPlafondStatsDTO body) {
        ApiResponse<LoanPlafondStatsDTO> res = new ApiResponse<>();
        res.setMsgHeader(new ErrorResponse(CODE_SUCCESS));
        res.setMsgBody(body);
        return res;
    }

    private ApiResponse<LoanPlafondStatsDTO> failure(String message) {
        ErrorResponse header = new ErrorResponse(CODE_FAILED);
        header.setErrorMessage(message);
        ApiResponse<LoanPlafondStatsDTO> res = new ApiResponse<>();
        res.setMsgHeader(header);
        res.setMsgBody(null);
        return res;
    }
}
