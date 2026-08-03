package com.m2e.dashboard.service.deposit.ca.Rest;

import com.m2e.dashboard.config.UserAndCorp;
import com.m2e.dashboard.dto.base.AccountListReq;
import com.m2e.dashboard.dto.common.ApiResponse;
import com.m2e.dashboard.dto.common.ErrorResponse;
import com.m2e.dashboard.dto.deposit.ca.response.DepositAcctListingDTO;
import com.m2e.dashboard.model.BTS.Request.Summary;
import com.m2e.dashboard.model.BTS.Response.Sumary.SummaryResponse;
import com.m2e.dashboard.service.overview.Refactor.KursService;
import com.m2e.dashboard.model.Trapi.Response.KursAllResponse;
import com.m2e.dashboard.sp.deposit.ca.response.DepositAcctSummarySpDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DepositAllAccountListService {

    private final BTSAccountClient btsAccountClient;
    private final KursService kursService;

    @Cacheable(
            value = "deposit-all-account",
            key = "#user.userId + '_' + #req.corpCifNo"
    )
    public ApiResponse<DepositAcctListingDTO> getAllAccounts(UserAndCorp user, AccountListReq req) {
        log.info("START getAllAccounts | CIF={} | userId={}", req.getCorpCifNo(), user.getUserId());

        // ===== STEP 1: Fetch & Cache Kurs
        fetchAndValidateKurs();

        // ===== STEP 2: Fetch BTS Account Summary =====
        Summary request = new Summary();
        request.setUserId(user.getUserId());
        request.setCif(req.getCorpCifNo());
        request.setLocale("en_US");
        request.setIndicator("R");

        SummaryResponse btsResp = btsAccountClient.getAccountSummary(request);
        validateResponse(btsResp);

        List<DepositAcctSummarySpDTO> allAccounts = new ArrayList<>();
        if (btsResp.getResponseData() != null && btsResp.getResponseData().getAssetList() != null) {
            btsResp.getResponseData().getAssetList().forEach(category -> {
                if (category.getProductList() != null) {
                    category.getProductList().forEach(product -> {
                        DepositAcctSummarySpDTO dto = new DepositAcctSummarySpDTO();
                        dto.setAcctName(product.getProductName());
                        dto.setAcctNumber(product.getAccNo());
                        dto.setCurrency(product.getCurrencyCodeIso());
                        dto.setLedgerBalance(product.getAmount());
                        dto.setAvailableBalance(product.getAmount());
                        dto.setAcctType(product.getAccountType());
                        allAccounts.add(dto);
                    });
                }
            });
        }

        DepositAcctListingDTO listing = new DepositAcctListingDTO();
        listing.setDatas(new com.m2e.dashboard.dto.common.SearchCollectionResponse<>(
                1, 1, -1, allAccounts.size()));
        listing.getDatas().setData(allAccounts);

        ApiResponse<DepositAcctListingDTO> response = new ApiResponse<>();
        response.setMsgHeader(new ErrorResponse("0"));
        response.setMsgBody(listing);
        return response;
    }

    private void fetchAndValidateKurs() {
        try {
            KursAllResponse kursResponse = kursService.getKursAll();
            if (kursResponse != null && kursResponse.getRespData() != null) {
                log.info("Kurs data ready. Total currencies available: {}",
                        kursResponse.getRespData().size());
            } else {
                log.warn("Kurs data not available or empty, continue without kurs");
            }
        } catch (Exception e) {
            log.error("Error fetching kurs: {}", e.getMessage(), e);
        }
    }

    private void validateResponse(SummaryResponse response) {
        if (response == null || response.getResponseData() == null) {
            throw new RuntimeException("Invalid BTS response");
        }
    }
}