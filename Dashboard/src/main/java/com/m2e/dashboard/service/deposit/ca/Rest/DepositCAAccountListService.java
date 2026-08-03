package com.m2e.dashboard.service.deposit.ca.Rest;

import com.m2e.dashboard.config.UserAndCorp;
import com.m2e.dashboard.dto.base.AccountListReq;
import com.m2e.dashboard.dto.common.ApiResponse;
import com.m2e.dashboard.dto.common.ErrorResponse;
import com.m2e.dashboard.dto.deposit.ca.response.DepositAcctListingDTO;
import com.m2e.dashboard.enums.AcctTypEnum;
import com.m2e.dashboard.model.BTS.Request.Summary;
import com.m2e.dashboard.model.BTS.Response.Sumary.*;
import com.m2e.dashboard.sp.deposit.ca.response.DepositAcctSummarySpDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class DepositCAAccountListService {

    private final BTSAccountClient btsAccountClient;
    private final RedisTemplate<String, Object> redisTemplate;

    // ===== REDIS CONFIG =====
    private static final String REDIS_KEY_PREFIX = "deposit-ca-account-list::";
    private static final long CACHE_TTL_MINUTES = 5;

    /**
     * CURRENT ACCOUNT LIST - MAIN FLOW (WITH REDIS CACHE)
     */
    public ApiResponse<DepositAcctListingDTO> getAccountList(UserAndCorp user, AccountListReq req) {

        // ===== STEP 1: BUILD REDIS KEY (userId + corpCifNo) =====
        String redisKey = REDIS_KEY_PREFIX + user.getUserId() + "_" + req.getCorpCifNo();

        // ===== STEP 2: CHECK REDIS CACHE =====
        DepositAcctListingDTO cachedResult = getFromCache(redisKey);
        if (cachedResult != null) {
            log.info("CA Account List found in Redis: {}", redisKey);
            return success(cachedResult);
        }

        log.info("CA Account List NOT in Redis: {}. Fetching from BTS...", redisKey);

        // ===== STEP 3: FETCH FROM BTS =====
        DepositAcctListingDTO result = fetchFromBTS(user, req);

        // ===== STEP 4: SAVE TO REDIS =====
        if (result != null && result.getDatas() != null) {
            saveToCache(redisKey, result);
        }

        return success(result);
    }

    private DepositAcctListingDTO fetchFromBTS(UserAndCorp user, AccountListReq req) {
        try {
            SummaryResponse btsResp = btsAccountClient.getAccountSummary(buildBTSRequest(user, req));
            return mapBtsToDepositListing(btsResp);
        } catch (Exception ex) {
            log.error("BTS call failed", ex);
            return null;
        }
    }

    /**
     * GET FROM REDIS CACHE
     */
    private DepositAcctListingDTO getFromCache(String key) {
        try {
            Object cached = redisTemplate.opsForValue().get(key);
            if (cached instanceof DepositAcctListingDTO) {
                return (DepositAcctListingDTO) cached;
            }
        } catch (Exception e) {
            log.warn("Failed to get from Redis: {}", e.getMessage());
        }
        return null;
    }

    /**
     * SAVE TO REDIS CACHE
     */
    private void saveToCache(String key, DepositAcctListingDTO data) {
        try {
            redisTemplate.opsForValue().set(key, data, CACHE_TTL_MINUTES, TimeUnit.MINUTES);
            log.info("CA Account List saved to Redis: {} (TTL: {} min)", key, CACHE_TTL_MINUTES);
        } catch (Exception e) {
            log.warn("Failed to save to Redis: {}", e.getMessage());
        }
    }

    /**
     * BUILD BTS REQUEST
     */
    private Summary buildBTSRequest(UserAndCorp user, AccountListReq reqFE) {
        Summary req = new Summary();
        req.setUserId(user.getUserId());
        req.setCif(reqFE.getCorpCifNo());
        req.setLocale("id_ID");
        req.setIndicator("R");
        return req;
    }

    /**
     * BUILD SUCCESS RESPONSE
     */
    private ApiResponse<DepositAcctListingDTO> success(DepositAcctListingDTO body) {
        ApiResponse<DepositAcctListingDTO> response = new ApiResponse<>();
        response.setMsgHeader(new ErrorResponse("0"));
        response.setMsgBody(body);
        return response;
    }

    /**
     * MAP BTS RESPONSE TO DEPOSIT LISTING DTO
     */
    private DepositAcctListingDTO mapBtsToDepositListing(SummaryResponse response) {
        DepositAcctListingDTO dto = new DepositAcctListingDTO();

        if (response == null ||
                response.getResponseData() == null ||
                response.getResponseData().getAssetList() == null) {
            return dto;
        }

        List<DepositAcctSummarySpDTO> accountList = new java.util.ArrayList<>();

        for (SummaryCategory category : response.getResponseData().getAssetList()) {
            if (category.getProductList() == null) continue;

            category.getProductList().stream()
                    .filter(p -> AcctTypEnum.CA.name().equalsIgnoreCase(p.getAccountType()))
                    .forEach(product -> {
                        DepositAcctSummarySpDTO acc = new DepositAcctSummarySpDTO();
                        acc.setAcctName(product.getProductName());
                        acc.setAcctNumber(product.getAccNo());
                        acc.setCurrency(product.getCurrencyCodeIso());
                        acc.setLedgerBalance(product.getAmount());
                        acc.setAvailableBalance(product.getAmount());
                        acc.setAcctType(product.getAccountType());
                        accountList.add(acc);
                    });
        }

        dto.setDatas(new com.m2e.dashboard.dto.common.SearchCollectionResponse<>(
                1, 1, -1, accountList.size()));
        dto.getDatas().setData(accountList);

        return dto;
    }
}