package com.m2e.dashboard.service.deposit.ca.Rest;

import com.m2e.dashboard.config.UserAndCorp;
import com.m2e.dashboard.dto.common.ApiResponse;
import com.m2e.dashboard.dto.common.ErrorResponse;
import com.m2e.dashboard.dto.deposit.ca.request.DepositAcctStatsReqDTO;
import com.m2e.dashboard.dto.deposit.ca.response.DepositAcctStatsDTO;
import com.m2e.dashboard.enums.AcctTypEnum;
import com.m2e.dashboard.model.BTS.Request.Summary;
import com.m2e.dashboard.model.BTS.Response.Sumary.SummaryCategory;
import com.m2e.dashboard.model.BTS.Response.Sumary.SummaryProduct;
import com.m2e.dashboard.model.BTS.Response.Sumary.SummaryResponse;
import com.m2e.dashboard.util.DateTimeUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DepositCAAccountStatsService {

    private final BTSAccountClient btsAccountClient;
    private final RedisTemplate<String, Object> redisTemplate;

    private static final String REDIS_KEY_PREFIX = "deposit:ca:stats:";
    private static final DecimalFormat FORMATTER = new DecimalFormat("#,##0.00");

    public ApiResponse<DepositAcctStatsDTO> getDepositCAAccountStats(
            UserAndCorp user,
            DepositAcctStatsReqDTO req) {

        ApiResponse<DepositAcctStatsDTO> apiResponse = new ApiResponse<>();
        DepositAcctStatsDTO depositAcctStatsDTO;

        // CASE 1: NON REALTIME → REDIS
        if (!Boolean.TRUE.equals(req.getRealTimeIndicator())) {
            String redisKey = REDIS_KEY_PREFIX + user.getUserId() + ":" + user.getCorpId();

            depositAcctStatsDTO = (DepositAcctStatsDTO) redisTemplate.opsForValue().get(redisKey);

            if (depositAcctStatsDTO == null) {
                // Jika Redis kosong, return default empty
                depositAcctStatsDTO = createEmptyStats();
            }

            apiResponse.setMsgHeader(new ErrorResponse("0"));
            apiResponse.setMsgBody(depositAcctStatsDTO);
            return apiResponse;
        }

        // CASE 2: REALTIME → BTS ONLY
        try {
            SummaryResponse btsResp = btsAccountClient.getAccountSummary(
                    buildBTSRequest(user)
            );

            // Map BTS response ke DepositAcctStatsDTO
            depositAcctStatsDTO = mapBtsToDepositStats(btsResp);

            // Set timestamp realtime
            depositAcctStatsDTO.setLastUpdatedDateTime(DateTimeUtil.getCurrentTime());
            depositAcctStatsDTO.setLastUpdatedDate(DateTimeUtil.getTodayDate());

            // Simpan ke Redis untuk cache
            String redisKey = REDIS_KEY_PREFIX + user.getUserId() + ":" + user.getCorpId();
            redisTemplate.opsForValue().set(redisKey, depositAcctStatsDTO);

        } catch (Exception ex) {
            log.error("BTS call failed for account stats", ex);

            // Fallback ke Redis jika BTS gagal
            String redisKey = REDIS_KEY_PREFIX + user.getUserId() + ":" + user.getCorpId();
            depositAcctStatsDTO = (DepositAcctStatsDTO) redisTemplate.opsForValue().get(redisKey);

            if (depositAcctStatsDTO == null) {
                depositAcctStatsDTO = createEmptyStats();
            }
        }

        apiResponse.setMsgHeader(new ErrorResponse("0"));
        apiResponse.setMsgBody(depositAcctStatsDTO);
        return apiResponse;
    }

    /**
     * BUILD BTS REQUEST
     */
    private Summary buildBTSRequest(UserAndCorp user) {
        Summary req = new Summary();
        req.setUserId(user.getUserId());
        req.setCif(user.getCorpId());
        req.setLocale("id_ID");
        req.setIndicator("R");

        return req;
    }

     // MAP BTS RESPONSE → DepositAcctStatsDTO
     // Menghitung total rekening, total ledger balance, total available balance

    private DepositAcctStatsDTO mapBtsToDepositStats(SummaryResponse response) {
        DepositAcctStatsDTO statsDTO = new DepositAcctStatsDTO();

        if (response == null
                || response.getResponseData() == null
                || response.getResponseData().getAssetList() == null) {
            return createEmptyStats();
        }

        int accountCount = 0;
        double totalLedgerBal = 0.0;
        double totalAvailableBal = 0.0;

        List<SummaryCategory> categories = response.getResponseData().getAssetList();

        for (SummaryCategory category : categories) {
            if (category.getProductList() == null) {
                continue;
            }

            // Filter hanya produk CA (Current Account)
            List<SummaryProduct> caProducts = category.getProductList().stream()
                    .filter(p -> AcctTypEnum.CA.name().equalsIgnoreCase(p.getAccountType()))
                    .toList();

            for (SummaryProduct product : caProducts) {
                accountCount++;

                // Parse amount string ke double
                try {
                    String amountStr = product.getAmount();
                    if (amountStr != null && !amountStr.isEmpty()) {
                        amountStr = amountStr.replaceAll(",", "");
                        double amount = Double.parseDouble(amountStr);
                        totalLedgerBal += amount;
                        totalAvailableBal += amount;
                    }
                } catch (NumberFormatException e) {
                    log.warn("Failed to parse amount for account: {}", product.getAccNo(), e);
                }
            }
        }

        // Set hasil perhitungan
        statsDTO.setNumberOfAcct(String.valueOf(accountCount));
        statsDTO.setTotalLedgerBal(FORMATTER.format(totalLedgerBal));
        statsDTO.setTotalAvailableBal(FORMATTER.format(totalAvailableBal));

        return statsDTO;
    }

    /**
     * CREATE EMPTY STATS (DEFAULT)
     */
    private DepositAcctStatsDTO createEmptyStats() {
        DepositAcctStatsDTO emptyStats = new DepositAcctStatsDTO();
        emptyStats.setNumberOfAcct("0");
        emptyStats.setTotalLedgerBal("0.00");
        emptyStats.setTotalAvailableBal("0.00");
        return emptyStats;
    }
}