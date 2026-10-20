package com.m2e.dashboard.service.deposit.fd.Rest;

import com.m2e.dashboard.config.CorpCifResolver;
import com.m2e.dashboard.config.UserAndCorp;
import com.m2e.dashboard.dto.common.ApiResponse;
import com.m2e.dashboard.dto.common.ErrorResponse;
import com.m2e.dashboard.dto.deposit.DepositFdStatsByCurrencyProjection;
import com.m2e.dashboard.dto.deposit.fd.request.DepositFDAcctStatsReqDTO;
import com.m2e.dashboard.dto.deposit.fd.response.DepositFDAcctStatsDTO;
import com.m2e.dashboard.dto.overview.Refactor.KursResolution;
import com.m2e.dashboard.repository.DepositFdRepository;
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
 * Statistik deposito berjangka dari DATAMART_DEPOSIT_FD, tanggal muat terakhir.
 * Dikonversi ke baseCurrency karena menjumlahkan lintas deposito.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DepositFDAccountStatsService {

    private final DepositFdRepository repository;
    private final CorpCifResolver corpCifResolver;
    private final KursResolver kursResolver;
    private final GraphCurrencyConverter graphConverter;
    private final DatamartDateResolver dateResolver;

    private static final String IDR = "IDR";
    private static final String CODE_SUCCESS = "0";
    private static final String CODE_FAILED = "99";

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DATETIME_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Cacheable(
            value = "depositFdStats",
            key = "#user.corpId + '_' "
                    + "+ (#req?.baseCurrency != null and !#req.baseCurrency.isBlank() "
                    + "   ? #req.baseCurrency.trim().toUpperCase() : 'IDR') + '_' "
                    + "+ T(java.time.LocalDate).now()",
            unless = "#result == null || #result.msgBody == null"
    )
    public ApiResponse<DepositFDAcctStatsDTO> getDepositFDAccountStats(
            UserAndCorp user, DepositFDAcctStatsReqDTO req) {

        String requested = (req != null) ? req.getBaseCurrency() : null;
        String targetCurrency = (requested != null && !requested.isBlank())
                ? requested.trim().toUpperCase() : IDR;

        String cifNo;
        try {
            cifNo = corpCifResolver.resolveCif(user.getCorpId());
        } catch (Exception e) {
            log.error("[FD-Stats] CIF gagal di-resolve corpId={}: {}", user.getCorpId(), e.getMessage());
            return failure("CIF tidak ditemukan untuk korporasi ini");
        }

        try {
            Optional<DatamartDateRange> range = dateResolver.fd();
            if (range.isEmpty()) {
                log.warn("[FD-Stats] Belum ada data FD di database");
                return success(buildDto(0L, BigDecimal.ZERO, targetCurrency));
            }
            DatamartDateRange r = range.get();

            log.info("[FD-Stats] START cif={} currency={} dataDate={}", cifNo, targetCurrency, r.dataDate());

            List<DepositFdStatsByCurrencyProjection> groups =
                    repository.getStatsByCurrency(cifNo, r.startOfDay(), r.endOfDay());

            if (groups.isEmpty()) {
                log.info("[FD-Stats] Tidak ada deposito untuk cif={} dataDate={}", cifNo, r.dataDate());
                return success(buildDto(0L, BigDecimal.ZERO, targetCurrency));
            }

            boolean needsConversion = groups.stream()
                    .anyMatch(g -> g.getCurrency() == null
                            || !targetCurrency.equalsIgnoreCase(g.getCurrency().trim()));

            Map<String, BigDecimal> rateMap = Map.of();
            if (needsConversion) {
                KursResolution kurs = kursResolver.resolve();
                if (kurs == null) {
                    log.error("[FD-Stats] Kurs tidak tersedia, cif={}", cifNo);
                    return failure("Kurs tidak tersedia untuk konversi ke " + targetCurrency);
                }
                rateMap = kurs.getRateMap();
                log.info("[FD-Stats] Kurs source={} pairs={} age={}h",
                        kurs.getSource(), rateMap.size(), kurs.getAgeHours());
            }

            long totalAccounts = 0L;
            BigDecimal totalPrincipal = BigDecimal.ZERO;

            for (DepositFdStatsByCurrencyProjection g : groups) {
                totalAccounts += (g.getNoOfAccounts() != null ? g.getNoOfAccounts() : 0L);

                BigDecimal amount = g.getTotalPrincipal() != null ? g.getTotalPrincipal() : BigDecimal.ZERO;
                BigDecimal converted = graphConverter.convert(amount, g.getCurrency(), targetCurrency, rateMap);

                if (converted == null) {
                    log.error("[FD-Stats] Gagal konversi {} {} -> {} untuk cif={}. Total dibatalkan.",
                            amount, g.getCurrency(), targetCurrency, cifNo);
                    return failure("Kurs " + g.getCurrency() + " ke " + targetCurrency + " tidak ditemukan");
                }

                totalPrincipal = totalPrincipal.add(converted);
            }

            log.info("[FD-Stats] SUCCESS cif={} accounts={} total={} {}",
                    cifNo, totalAccounts, totalPrincipal.toPlainString(), targetCurrency);

            return success(buildDto(totalAccounts, totalPrincipal, targetCurrency));

        } catch (Exception e) {
            log.error("[FD-Stats] FAILED cif={}: {}", cifNo, e.getMessage(), e);
            return failure("Data deposito tidak dapat diproses saat ini");
        }
    }

    private DepositFDAcctStatsDTO buildDto(long count, BigDecimal total, String currency) {

        DepositFDAcctStatsDTO dto = new DepositFDAcctStatsDTO();
        LocalDateTime updatedAt = LocalDateTime.now();

        dto.setNumberOfAcct(String.valueOf(count));
        dto.setTotalOfCertificates(String.valueOf(count));
        dto.setConsolidatedPrincipalAmt(format(total));
        dto.setCurrency(currency);
        dto.setLastUpdatedDateTime(updatedAt.format(DATETIME_FMT));
        dto.setLastUpdatedDate(updatedAt.format(DATE_FMT));
        return dto;
    }

    /** DecimalFormat tidak thread-safe, jadi dibuat baru tiap pemanggilan. */
    private String format(BigDecimal value) {
        DecimalFormat formatter = new DecimalFormat("#,##0.00",
                DecimalFormatSymbols.getInstance(Locale.US));
        return formatter.format(value.setScale(2, RoundingMode.HALF_UP));
    }

    private ApiResponse<DepositFDAcctStatsDTO> success(DepositFDAcctStatsDTO body) {
        ApiResponse<DepositFDAcctStatsDTO> res = new ApiResponse<>();
        res.setMsgHeader(new ErrorResponse(CODE_SUCCESS));
        res.setMsgBody(body);
        return res;
    }

    private ApiResponse<DepositFDAcctStatsDTO> failure(String message) {
        ErrorResponse header = new ErrorResponse(CODE_FAILED);
        header.setErrorMessage(message);
        ApiResponse<DepositFDAcctStatsDTO> res = new ApiResponse<>();
        res.setMsgHeader(header);
        res.setMsgBody(null);
        return res;
    }
}