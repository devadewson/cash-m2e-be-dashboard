package com.m2e.dashboard.service.deposit.fd.Rest;

import com.m2e.dashboard.config.CorpCifResolver;
import com.m2e.dashboard.config.UserAndCorp;
import com.m2e.dashboard.dto.common.ApiResponse;
import com.m2e.dashboard.dto.common.ErrorResponse;
import com.m2e.dashboard.dto.deposit.fd.request.DepositFDAcctStatsReqDTO;
import com.m2e.dashboard.dto.deposit.fd.response.DepositFDAcctStatsDTO;
import com.m2e.dashboard.enums.AcctTypEnum;
import com.m2e.dashboard.model.BTS.Request.Summary;
import com.m2e.dashboard.model.BTS.Response.Sumary.SummaryCategory;
import com.m2e.dashboard.model.BTS.Response.Sumary.SummaryProduct;
import com.m2e.dashboard.model.BTS.Response.Sumary.SummaryResponse;
import com.m2e.dashboard.service.deposit.ca.Rest.BTSAccountClient;
import com.m2e.dashboard.util.DateTimeUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class DepositFDAccountStatsService {

    private final BTSAccountClient btsAccountClient;
    private final RedisTemplate<String, Object> redisTemplate;
    private final CorpCifResolver corpCifResolver;

    private static final String REDIS_KEY_PREFIX = "deposit:fd:stats:";
    private static final long CACHE_TTL_MINUTES = 15;

    public ApiResponse<DepositFDAcctStatsDTO> getDepositFDAccountStats(
            UserAndCorp user, DepositFDAcctStatsReqDTO req) {

        String cifNo = corpCifResolver.resolveCif(user.getCorpId());
        String redisKey = REDIS_KEY_PREFIX + user.getUserId() + ":" + cifNo;

        // CASE 1: non-realtime, ambil dari cache
        if (!Boolean.TRUE.equals(req.getRealTimeIndicator())) {
            DepositFDAcctStatsDTO cached = readFromCache(redisKey);
            return success(cached != null ? cached : createEmptyStats());
        }

        // CASE 2: realtime, ambil dari BTS
        try {
            SummaryResponse btsResp = btsAccountClient.getAccountSummary(buildBTSRequest(user, cifNo));

            DepositFDAcctStatsDTO stats = mapBtsToDepositFDStats(btsResp, cifNo);
            stats.setLastUpdatedDateTime(DateTimeUtil.getCurrentTime());
            stats.setLastUpdatedDate(DateTimeUtil.getTodayDate());

            writeToCache(redisKey, stats);
            return success(stats);

        } catch (Exception ex) {
            log.error("BTS call failed for FD account stats, cif={}", cifNo, ex);

            DepositFDAcctStatsDTO cached = readFromCache(redisKey);
            if (cached != null) {
                log.warn("Serving stale cached FD stats for cif={}", cifNo);
                return success(cached);
            }
            return failure("Data deposito tidak tersedia saat ini");
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

    private DepositFDAcctStatsDTO mapBtsToDepositFDStats(SummaryResponse response, String cifNo) {
        if (response == null
                || response.getResponseData() == null
                || response.getResponseData().getAssetList() == null) {
            log.warn("BTS response kosong untuk cif={}", cifNo);
            return createEmptyStats();
        }

        int accountCount = 0;
        int totalScanned = 0;
        BigDecimal totalPrincipal = BigDecimal.ZERO;
        String firstCurrency = null;
        boolean mixedCurrency = false;

        List<SummaryCategory> categories = response.getResponseData().getAssetList();

        for (SummaryCategory category : categories) {
            if (category.getProductList() == null) {
                continue;
            }

            for (SummaryProduct product : category.getProductList()) {
                totalScanned++;

                // TODO hapus setelah nilai accountType terverifikasi
                log.info("BTS product: accountType=[{}] accNo=[{}] currency=[{}] amount=[{}]",
                        product.getAccountType(), product.getAccNo(),
                        product.getCurrencyCodeIso(), product.getAmount());

                if (!isFixedDeposit(product.getAccountType())) {
                    continue;
                }

                accountCount++;

                String currency = product.getCurrencyCodeIso();
                if (firstCurrency == null) {
                    firstCurrency = currency;
                } else if (currency != null && !firstCurrency.equalsIgnoreCase(currency)) {
                    mixedCurrency = true;
                }

                totalPrincipal = totalPrincipal.add(
                        parseAmount(product.getAmount(), product.getAccNo()));
            }
        }

        if (mixedCurrency) {
            log.error("MIXED CURRENCY pada FD stats cif={}. Total dijumlah mentah tanpa konversi.", cifNo);
        }
        if (accountCount == 0 && totalScanned > 0) {
            log.warn("cif={}: {} rekening di-scan, tidak ada yang bertipe {}. Cek nilai accountType dari BTS.",
                    cifNo, totalScanned, AcctTypEnum.TD.name());
        }

        DepositFDAcctStatsDTO statsDTO = new DepositFDAcctStatsDTO();
        statsDTO.setNumberOfAcct(String.valueOf(accountCount));
        statsDTO.setTotalOfCertificates(String.valueOf(accountCount));
        statsDTO.setConsolidatedPrincipalAmt(format(totalPrincipal));
        return statsDTO;
    }

    private boolean isFixedDeposit(String acctType) {
        return acctType != null
                && AcctTypEnum.TD.name().equalsIgnoreCase(acctType.trim());
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

    private DepositFDAcctStatsDTO readFromCache(String redisKey) {
        try {
            Object cached = redisTemplate.opsForValue().get(redisKey);
            if (cached instanceof DepositFDAcctStatsDTO dto) {
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

    private void writeToCache(String redisKey, DepositFDAcctStatsDTO stats) {
        try {
            redisTemplate.opsForValue().set(redisKey, stats, CACHE_TTL_MINUTES, TimeUnit.MINUTES);
        } catch (Exception e) {
            log.warn("Gagal simpan cache {}: {}", redisKey, e.getMessage());
        }
    }

    private ApiResponse<DepositFDAcctStatsDTO> success(DepositFDAcctStatsDTO body) {
        ApiResponse<DepositFDAcctStatsDTO> res = new ApiResponse<>();
        res.setMsgHeader(new ErrorResponse("0"));
        res.setMsgBody(body);
        return res;
    }

    private ApiResponse<DepositFDAcctStatsDTO> failure(String message) {
        log.warn("Returning error response: {}", message);
        ApiResponse<DepositFDAcctStatsDTO> res = new ApiResponse<>();
        res.setMsgHeader(new ErrorResponse("99"));
        return res;
    }

    private DepositFDAcctStatsDTO createEmptyStats() {
        DepositFDAcctStatsDTO emptyStats = new DepositFDAcctStatsDTO();
        emptyStats.setNumberOfAcct("0");
        emptyStats.setTotalOfCertificates("0");
        emptyStats.setConsolidatedPrincipalAmt("0.00");
        return emptyStats;
    }
}