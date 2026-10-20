package com.m2e.dashboard.service.deposit.ca.Rest;

import com.m2e.dashboard.config.CorpCifResolver;
import com.m2e.dashboard.config.UserAndCorp;
import com.m2e.dashboard.dto.common.ApiResponse;
import com.m2e.dashboard.dto.common.ErrorResponse;
import com.m2e.dashboard.dto.deposit.ca.request.DepositAcctStatsReqDTO;
import com.m2e.dashboard.dto.deposit.ca.response.DepositAcctStatsDTO;
import com.m2e.dashboard.dto.overview.Refactor.KursResolution;
import com.m2e.dashboard.enums.AcctTypEnum;
import com.m2e.dashboard.model.BTS.Request.Summary;
import com.m2e.dashboard.model.BTS.Response.Sumary.SummaryCategory;
import com.m2e.dashboard.model.BTS.Response.Sumary.SummaryProduct;
import com.m2e.dashboard.model.BTS.Response.Sumary.SummaryResponse;
import com.m2e.dashboard.service.overview.AssetGraph.GraphCurrencyConverter;
import com.m2e.dashboard.service.overview.JobKursService.KursResolver;
import com.m2e.dashboard.util.DateTimeUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class DepositSAAccountStatsService {

    private final BTSAccountClient btsAccountClient;
    private final RedisTemplate<String, Object> redisTemplate;
    private final CorpCifResolver corpCifResolver;
    private final KursResolver kursResolver;
    private final GraphCurrencyConverter graphConverter;

    private static final String IDR = "IDR";
    private static final String REDIS_KEY_PREFIX = "deposit:sa:stats:";
    private static final long CACHE_TTL_MINUTES = 15;
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DATETIME_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");


    public ApiResponse<DepositAcctStatsDTO> getDepositSAAccountStats(
            UserAndCorp user, DepositAcctStatsReqDTO req) {

        String cifNo = corpCifResolver.resolveCif(user.getCorpId());
        String targetCurrency = (req.getBaseCurrency() != null && !req.getBaseCurrency().isBlank())
                ? req.getBaseCurrency().trim().toUpperCase() : IDR;

        String redisKey = REDIS_KEY_PREFIX + user.getUserId() + ":" + cifNo + ":" + targetCurrency;

        // CASE 1: non-realtime, ambil dari cache
        if (!Boolean.TRUE.equals(req.getRealTimeIndicator())) {
            DepositAcctStatsDTO cached = readFromCache(redisKey);
            return success(cached != null ? cached : createEmptyStats(targetCurrency));
        }

        // CASE 2: realtime, ambil dari BTS
        try {
            SummaryResponse btsResp = btsAccountClient.getAccountSummary(buildBTSRequest(user, cifNo));

            DepositAcctStatsDTO stats = mapBtsToDepositStats(btsResp, cifNo, targetCurrency);
            if (stats == null) {
                return failure("Kurs tidak tersedia untuk konversi ke " + targetCurrency);
            }

            LocalDateTime updatedAt = LocalDateTime.now();
            stats.setLastUpdatedDateTime(updatedAt.format(DATETIME_FMT));
            stats.setLastUpdatedDate(updatedAt.format(DATE_FMT));

            writeToCache(redisKey, stats);
            return success(stats);

        } catch (Exception ex) {
            log.error("BTS call failed for SA account stats, cif={}", cifNo, ex);

            DepositAcctStatsDTO cached = readFromCache(redisKey);
            if (cached != null) {
                log.warn("Serving stale cached SA stats for cif={}", cifNo);
                return success(cached);
            }
            return failure("Data rekening tidak tersedia saat ini");
        }
    }

    private Summary buildBTSRequest(UserAndCorp user, String cifNo) {
        Summary req = new Summary();
        req.setUserId(user.getUserId());
        req.setCif(cifNo);
        req.setLocale("id_ID");
        req.setIndicator("R");
        return req;
    }

    /**
     * @return null kalau konversi gagal, supaya caller bisa mengembalikan error
     *         alih-alih total yang tidak lengkap.
     */
    private DepositAcctStatsDTO mapBtsToDepositStats(
            SummaryResponse response, String cifNo, String targetCurrency) {

        if (response == null
                || response.getResponseData() == null
                || response.getResponseData().getAssetList() == null) {
            log.warn("BTS response kosong untuk cif={}", cifNo);
            return createEmptyStats(targetCurrency);
        }

        List<SummaryProduct> saProducts = new ArrayList<>();
        int totalScanned = 0;
        boolean needsConversion = false;

        for (SummaryCategory category : response.getResponseData().getAssetList()) {
            if (category.getProductList() == null) {
                continue;
            }
            for (SummaryProduct product : category.getProductList()) {
                totalScanned++;
                if (!isSavingAccount(product.getAccountType())) {
                    continue;
                }
                saProducts.add(product);

                String currency = product.getCurrencyCodeIso();
                if (currency != null && !targetCurrency.equalsIgnoreCase(currency.trim())) {
                    needsConversion = true;
                }
            }
        }

        if (saProducts.isEmpty()) {
            if (totalScanned > 0) {
                log.warn("cif={}: {} rekening di-scan, tidak ada yang bertipe {}. "
                                + "Cek nilai accountType dari BTS.",
                        cifNo, totalScanned, AcctTypEnum.SA.name());
            }
            return createEmptyStats(targetCurrency);
        }

        Map<String, BigDecimal> rateMap = Map.of();
        if (needsConversion) {
            KursResolution kurs = kursResolver.resolve();
            if (kurs == null) {
                log.error("Kurs tidak tersedia dari Trapi maupun DB, cif={} target={}",
                        cifNo, targetCurrency);
                return null;
            }
            rateMap = kurs.getRateMap();
            log.info("SA stats kurs source={} pairs={} age={}h",
                    kurs.getSource(), rateMap.size(), kurs.getAgeHours());
        }

        BigDecimal totalLedger = BigDecimal.ZERO;
        BigDecimal totalAvailable = BigDecimal.ZERO;

        for (SummaryProduct product : saProducts) {
            BigDecimal amount = parseAmount(product.getAmount(), product.getAccNo());
            String currency = product.getCurrencyCodeIso();

            BigDecimal converted = graphConverter.convert(amount, currency, targetCurrency, rateMap);

            if (converted == null) {
                log.error("Gagal konversi {} {} -> {} untuk rekening {}. Total dibatalkan.",
                        amount, currency, targetCurrency, product.getAccNo());
                return null;
            }

            totalLedger = totalLedger.add(converted);
            totalAvailable = totalAvailable.add(converted);
        }

        log.info("SA stats cif={} accounts={} total={} {}",
                cifNo, saProducts.size(), totalLedger.toPlainString(), targetCurrency);

        DepositAcctStatsDTO statsDTO = new DepositAcctStatsDTO();
        statsDTO.setNumberOfAcct(String.valueOf(saProducts.size()));
        statsDTO.setTotalLedgerBal(format(totalLedger));
        statsDTO.setTotalAvailableBal(format(totalAvailable));
        statsDTO.setCurrency(targetCurrency);
        return statsDTO;
    }

    private boolean isSavingAccount(String acctType) {
        return acctType != null
                && AcctTypEnum.SA.name().equalsIgnoreCase(acctType.trim());
    }

    private BigDecimal parseAmount(String value, String acctNo) {
        if (value == null || value.trim().isEmpty()) {
            return BigDecimal.ZERO;
        }
        try {
            return new BigDecimal(value.replace(",", "").trim());
        } catch (NumberFormatException e) {
            log.warn("Gagal parse amount [{}] untuk rekening {}", value, acctNo);
            return BigDecimal.ZERO;
        }
    }

    /** DecimalFormat tidak thread-safe, jadi dibuat baru tiap pemanggilan. */
    private String format(BigDecimal value) {
        DecimalFormat formatter = new DecimalFormat("#,##0.00",
                DecimalFormatSymbols.getInstance(Locale.US));
        return formatter.format(value.setScale(2, RoundingMode.HALF_UP));
    }

    private DepositAcctStatsDTO readFromCache(String redisKey) {
        try {
            Object cached = redisTemplate.opsForValue().get(redisKey);
            if (cached instanceof DepositAcctStatsDTO dto) {
                return dto;
            }
            if (cached != null) {
                log.warn("Cache type mismatch untuk key {}: {}", redisKey, cached.getClass().getName());
                redisTemplate.delete(redisKey);
            }
        } catch (Exception e) {
            log.warn("Gagal baca cache {}: {}", redisKey, e.getMessage());
        }
        return null;
    }

    private void writeToCache(String redisKey, DepositAcctStatsDTO stats) {
        try {
            redisTemplate.opsForValue().set(redisKey, stats, CACHE_TTL_MINUTES, TimeUnit.MINUTES);
        } catch (Exception e) {
            log.warn("Gagal simpan cache {}: {}", redisKey, e.getMessage());
        }
    }

    private ApiResponse<DepositAcctStatsDTO> success(DepositAcctStatsDTO body) {
        ApiResponse<DepositAcctStatsDTO> res = new ApiResponse<>();
        res.setMsgHeader(new ErrorResponse("0"));
        res.setMsgBody(body);
        return res;
    }

    private ApiResponse<DepositAcctStatsDTO> failure(String message) {
        log.warn("Returning error response: {}", message);
        ApiResponse<DepositAcctStatsDTO> res = new ApiResponse<>();
        res.setMsgHeader(new ErrorResponse("99"));
        return res;
    }

    private DepositAcctStatsDTO createEmptyStats(String currency) {
        DepositAcctStatsDTO emptyStats = new DepositAcctStatsDTO();
        emptyStats.setNumberOfAcct("0");
        emptyStats.setTotalLedgerBal("0.00");
        emptyStats.setTotalAvailableBal("0.00");
        emptyStats.setCurrency(currency);
        return emptyStats;
    }
}