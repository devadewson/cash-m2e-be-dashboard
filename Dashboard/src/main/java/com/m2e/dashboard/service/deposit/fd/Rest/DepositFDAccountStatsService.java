package com.m2e.dashboard.service.deposit.fd.Rest;

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

import java.text.DecimalFormat;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DepositFDAccountStatsService {

    private final BTSAccountClient btsAccountClient;
    private final RedisTemplate<String, Object> redisTemplate;

    private static final String REDIS_KEY_PREFIX = "deposit:fd:stats:";
    private static final DecimalFormat FORMATTER = new DecimalFormat("#,##0.00");

    public ApiResponse<DepositFDAcctStatsDTO> getDepositFDAccountStats(
            UserAndCorp user,
            DepositFDAcctStatsReqDTO req) {

        ApiResponse<DepositFDAcctStatsDTO> apiResponse = new ApiResponse<>();
        DepositFDAcctStatsDTO depositFDAcctStatsDTO;

        // CASE 1: NON REALTIME → REDIS
        if (!Boolean.TRUE.equals(req.getRealTimeIndicator())) {
            String redisKey = REDIS_KEY_PREFIX + user.getUserId() + ":" + user.getCorpId();

            depositFDAcctStatsDTO = (DepositFDAcctStatsDTO) redisTemplate.opsForValue().get(redisKey);

            if (depositFDAcctStatsDTO == null) {
                depositFDAcctStatsDTO = createEmptyStats();
            }

            apiResponse.setMsgHeader(new ErrorResponse("0"));
            apiResponse.setMsgBody(depositFDAcctStatsDTO);
            return apiResponse;
        }

        // CASE 2: REALTIME → BTS ONLY
        try {
            SummaryResponse btsResp = btsAccountClient.getAccountSummary(
                    buildBTSRequest(user)
            );

            // Map BTS response ke DepositFDAcctStatsDTO
            depositFDAcctStatsDTO = mapBtsToDepositFDStats(btsResp);

            // Set timestamp realtime
            depositFDAcctStatsDTO.setLastUpdatedDateTime(DateTimeUtil.getCurrentTime());
            depositFDAcctStatsDTO.setLastUpdatedDate(DateTimeUtil.getTodayDate());

            // Simpan ke Redis untuk cache
            String redisKey = REDIS_KEY_PREFIX + user.getUserId() + ":" + user.getCorpId();
            redisTemplate.opsForValue().set(redisKey, depositFDAcctStatsDTO);

        } catch (Exception ex) {
            log.error("BTS call failed for FD account stats", ex);

            // Fallback ke Redis jika BTS gagal
            String redisKey = REDIS_KEY_PREFIX + user.getUserId() + ":" + user.getCorpId();
            depositFDAcctStatsDTO = (DepositFDAcctStatsDTO) redisTemplate.opsForValue().get(redisKey);

            if (depositFDAcctStatsDTO == null) {
                depositFDAcctStatsDTO = createEmptyStats();
            }
        }

        apiResponse.setMsgHeader(new ErrorResponse("0"));
        apiResponse.setMsgBody(depositFDAcctStatsDTO);
        return apiResponse;
    }


    private Summary buildBTSRequest(UserAndCorp user) {
        Summary req = new Summary();
        req.setUserId(user.getUserId());
        req.setCif(user.getCorpId());
        req.setLocale("id_ID");
        req.setIndicator("R");

        return req;
    }

    /**
     * MAP BTS RESPONSE → DepositFDAcctStatsDTO
     * Menghitung total sertifikat, total principal amount
     */
    private DepositFDAcctStatsDTO mapBtsToDepositFDStats(SummaryResponse response) {
        DepositFDAcctStatsDTO statsDTO = new DepositFDAcctStatsDTO();

        if (response == null
                || response.getResponseData() == null
                || response.getResponseData().getAssetList() == null) {
            return createEmptyStats();
        }

        int accountCount = 0;
        double totalPrincipal = 0.0;

        List<SummaryCategory> categories = response.getResponseData().getAssetList();

        for (SummaryCategory category : categories) {
            if (category.getProductList() == null) {
                continue;
            }

            // Filter hanya produk FD (Fixed Deposit / Time Deposit)
            List<SummaryProduct> fdProducts = category.getProductList().stream()
                    .filter(p -> AcctTypEnum.TD.name().equalsIgnoreCase(p.getAccountType()))
                    .toList();

            for (SummaryProduct product : fdProducts) {
                accountCount++;

                // Parse amount string ke double
                try {
                    String amountStr = product.getAmount();
                    if (amountStr != null && !amountStr.isEmpty()) {
                        amountStr = amountStr.replaceAll(",", "");
                        double amount = Double.parseDouble(amountStr);
                        totalPrincipal += amount;
                    }
                } catch (NumberFormatException e) {
                    log.warn("Failed to parse amount for account: {}", product.getAccNo(), e);
                }
            }
        }

        // Set hasil perhitungan sesuai field FD DTO
        statsDTO.setNumberOfAcct(String.valueOf(accountCount));
        statsDTO.setTotalOfCertificates(String.valueOf(accountCount));
        statsDTO.setConsolidatedPrincipalAmt(FORMATTER.format(totalPrincipal));

        return statsDTO;
    }

    /**
     * CREATE EMPTY STATS (DEFAULT)
     */
    private DepositFDAcctStatsDTO createEmptyStats() {
        DepositFDAcctStatsDTO emptyStats = new DepositFDAcctStatsDTO();
        emptyStats.setNumberOfAcct("0");
        emptyStats.setTotalOfCertificates("0");
        emptyStats.setConsolidatedPrincipalAmt("0.00");
        return emptyStats;
    }
}