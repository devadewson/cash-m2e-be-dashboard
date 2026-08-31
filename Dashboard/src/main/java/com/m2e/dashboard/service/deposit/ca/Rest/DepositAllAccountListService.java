package com.m2e.dashboard.service.deposit.ca.Rest;

import com.m2e.dashboard.config.UserAndCorp;
import com.m2e.dashboard.dto.base.AccountListReq;
import com.m2e.dashboard.dto.common.ApiResponse;
import com.m2e.dashboard.dto.common.ErrorResponse;
import com.m2e.dashboard.dto.deposit.ca.response.DepositAcctListingDTO;
import com.m2e.dashboard.model.BTS.Request.Summary;
import com.m2e.dashboard.model.BTS.Response.Sumary.SummaryResponse;
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

    @Cacheable(
            value = "deposit-all-account",
            key = "#user.userId + '_' + #req.corpCifNo",
            unless = "#result == null || #result.msgBody == null"
    )
    public ApiResponse<DepositAcctListingDTO> getAllAccounts(UserAndCorp user, AccountListReq req) {
        log.info("START getAllAccounts | CIF={} | userId={}", req.getCorpCifNo(), user.getUserId());

        Summary request = new Summary();
        request.setUserId(user.getUserId());
        request.setCif(req.getCorpCifNo());
        request.setLocale("en_US");
        request.setIndicator("R");

        SummaryResponse btsResp = btsAccountClient.getAccountSummary(request);
        validateResponse(btsResp);

        List<DepositAcctSummarySpDTO> allAccounts = new ArrayList<>();
        if (btsResp.getResponseData().getAssetList() != null) {
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

        log.info("Fetched {} accounts from BTS | CIF={}", allAccounts.size(), req.getCorpCifNo());

        DepositAcctListingDTO listing = new DepositAcctListingDTO();
        listing.setDatas(new com.m2e.dashboard.dto.common.SearchCollectionResponse<>(
                1, 1, -1, allAccounts.size()));
        listing.getDatas().setData(allAccounts);

        ApiResponse<DepositAcctListingDTO> response = new ApiResponse<>();
        response.setMsgHeader(new ErrorResponse("0"));
        response.setMsgBody(listing);
        return response;
    }

    private void validateResponse(SummaryResponse response) {
        if (response == null || response.getResponseData() == null) {
            throw new RuntimeException("Invalid BTS response");
        }
    }
}