package com.m2e.dashboard.service.deposit.fd.Rest;

import com.m2e.dashboard.config.UserAndCorp;
import com.m2e.dashboard.dto.base.AccountListReq;
import com.m2e.dashboard.dto.common.ApiResponse;
import com.m2e.dashboard.dto.common.ErrorResponse;
import com.m2e.dashboard.dto.deposit.ca.response.DepositAcctListingDTO;
import com.m2e.dashboard.enums.AcctTypEnum;
import com.m2e.dashboard.model.BTS.Request.Summary;
import com.m2e.dashboard.model.BTS.Response.Sumary.*;
import com.m2e.dashboard.service.deposit.ca.Rest.BTSAccountClient;
import com.m2e.dashboard.sp.deposit.ca.response.DepositAcctSummarySpDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class DepositFDAccountListService {

    private final BTSAccountClient btsAccountClient;
    private final RedisTemplate<String, Object> redisTemplate;

    // ===== REDIS CONFIG =====
    private static final String REDIS_KEY_PREFIX = "deposit-fd-account-list::";
    private static final long CACHE_TTL_MINUTES = 5;

    /**
     * FIXED DEPOSIT ACCOUNT LIST - MAIN FLOW (WITH REDIS CACHE)
     */
    public ApiResponse<DepositAcctListingDTO> getAccountList(
            UserAndCorp user,
            AccountListReq req) {

        log.info("START getAccountList | CIF={} | userId={}",
                req.getCorpCifNo(), user.getUserId());

        // ===== STEP 1: BUILD REDIS KEY =====
        String redisKey = REDIS_KEY_PREFIX + user.getUserId() + "_" + req.getCorpCifNo();
        log.debug("Redis Key: {}", redisKey);

        // ===== STEP 2: CHECK REDIS CACHE =====
        DepositAcctListingDTO cachedResult = getFromCache(redisKey);
        if (cachedResult != null) {
            log.info("FD Account List found in Redis: {}", redisKey);
            return success(cachedResult);
        }

        log.info("FD Account List NOT in Redis: {}. Fetching from BTS...", redisKey);

        try {
            // ===== STEP 3: BUILD BTS REQUEST =====
            Summary request = buildBTSRequest(user, req);
            log.info("BTS Request built | userId={} | cif={}",
                    request.getUserId(), request.getCif());

            // ===== STEP 4: CALL BTS =====
            log.info("Calling BTS Account Summary...");
            SummaryResponse btsResp = btsAccountClient.getAccountSummary(request);
            log.info("BTS Response received successfully");

            // ===== STEP 5: VALIDATE =====
            validateResponse(btsResp);

            // ===== STEP 6: MAP RESPONSE =====
            log.info("Mapping BTS response to DTO");
            DepositAcctListingDTO result = mapBtsToDepositListing(btsResp);

            // ===== STEP 7: SAVE TO REDIS =====
            if (result != null && result.getDatas() != null) {
                saveToCache(redisKey, result);
            }

            return success(result);

        } catch (Exception ex) {
            log.error("FAILED getAccountList | CIF={} | userId={}",
                    req.getCorpCifNo(), user.getUserId(), ex);
            throw new RuntimeException(
                    "Failed to retrieve FD Account List from BTS", ex);
        }
    }

    /**
     * GET FROM REDIS CACHE
     */
    private DepositAcctListingDTO getFromCache(String key) {
        try {
            Object cached = redisTemplate.opsForValue().get(key);
            if (cached instanceof DepositAcctListingDTO) {
                log.debug("Cache hit: {}", key);
                return (DepositAcctListingDTO) cached;
            }
        } catch (Exception e) {
            log.warn("Failed to get from Redis cache: {}", e.getMessage());
        }
        return null;
    }

    /**
     * SAVE TO REDIS CACHE
     */
    private void saveToCache(String key, DepositAcctListingDTO data) {
        try {
            redisTemplate.opsForValue().set(key, data, CACHE_TTL_MINUTES, TimeUnit.MINUTES);
            log.info("FD Account List saved to Redis: {} (TTL: {} min)", key, CACHE_TTL_MINUTES);
        } catch (Exception e) {
            log.warn("Failed to save to Redis cache: {}", e.getMessage());
        }
    }

    /**
     * BUILD BTS REQUEST
     */
    private Summary buildBTSRequest(UserAndCorp user, AccountListReq reqFE) {
        log.debug("Building BTS Request | CIF={} | userId={}",
                reqFE.getCorpCifNo(), user.getUserId());

        Summary req = new Summary();
        req.setUserId(user.getUserId());
        req.setCif(reqFE.getCorpCifNo());
        req.setLocale("id_ID");
        req.setIndicator("R");

        return req;
    }

    /**
     * VALIDATE BTS RESPONSE
     */
    private void validateResponse(SummaryResponse response) {
        log.debug("Validating BTS response...");

        if (response == null) {
            log.error("BTS response is NULL");
            throw new RuntimeException("BTS response is null");
        }

        if (response.getResponseData() == null) {
            log.error("BTS responseData is NULL");
            throw new RuntimeException("BTS responseData is null");
        }

        log.debug("BTS response validation PASSED");
    }

    /**
     * MAP BTS RESPONSE TO FD DEPOSIT LISTING DTO
     */
    private DepositAcctListingDTO mapBtsToDepositListing(SummaryResponse response) {
        DepositAcctListingDTO dto = new DepositAcctListingDTO();
        List<DepositAcctSummarySpDTO> accountList = new ArrayList<>();

        if (response.getResponseData().getAssetList() == null) {
            log.warn("No assetList found in BTS response");
            return dto;
        }

        for (SummaryCategory category : response.getResponseData().getAssetList()) {
            if (category.getProductList() == null) continue;

            category.getProductList().stream()
                    .filter(p -> AcctTypEnum.TD.name().equalsIgnoreCase(p.getAccountType()))
                    .forEach(product -> {
                        try {
                            DepositAcctSummarySpDTO acc = new DepositAcctSummarySpDTO();
                            acc.setAcctName(product.getProductName());
                            acc.setAcctNumber(product.getAccNo());
                            acc.setCurrency(product.getCurrencyCodeIso());
                            acc.setLedgerBalance(product.getAmount());
                            acc.setAvailableBalance(product.getAmount());
                            acc.setAcctType(product.getAccountType());
                            accountList.add(acc);

                            log.debug("Mapped FD account | accNo={}", product.getAccNo());
                        } catch (Exception mapEx) {
                            log.error("Mapping error | accNo={}", product.getAccNo(), mapEx);
                        }
                    });
        }

        dto.setDatas(new com.m2e.dashboard.dto.common.SearchCollectionResponse<>(
                1, 1, -1, accountList.size()));
        dto.getDatas().setData(accountList);

        log.info("Mapping complete | FD accounts found={}", accountList.size());
        return dto;
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
}