package com.m2e.dashboard.controller.maintenance;

import com.m2e.dashboard.auth.JwtUtil;
import com.m2e.dashboard.config.UserAndCorp;
import com.m2e.dashboard.dto.common.ApiResponse;
import com.m2e.dashboard.dto.common.ErrorResponse;
import com.m2e.dashboard.dto.maintenance.request.*;
import com.m2e.dashboard.dto.maintenance.response.*;
import com.m2e.dashboard.dto.task.response.EditAcctNameApprovalDTO;
import com.m2e.dashboard.dto.trade.TradeMaintAccountListReqDTO;
import com.m2e.dashboard.service.maintenance.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Account Name Administration", description = "APIs for maintaining portfolio account name configurations")
@RestController
@RequestMapping("admin/mt/acct/v1")
@RequiredArgsConstructor
public class AccountNameMTController {
    private final MTLNService mtLoanTlfService;
    private final MTINVUnitTrustService mtinvUnitTrustService;
    private final MTAcctNameService mtAcctNameService;
    private final JwtUtil jwtUtil;
    private final MTAutoFinanceService mtAutoFinanceService;
    private final MTOtherBankService mtOtherBankService;
    private final MTDepositService mtDepositService;
    private final MTTradeService mtTradeService;

    @Operation(
            summary = "Get sub-product listing.",
            description = "Retrieves a list of sub-products for each product.")
    @PostMapping("/sub-product/listing")
    public ResponseEntity<ApiResponse<List<SubProductDTO>>> subProductListing(@RequestHeader(HttpHeaders.AUTHORIZATION) String auth, @RequestBody SubProductListReqDTO req) {
        return ResponseEntity.ok(mtAcctNameService.getSubProducts(req));
    }

    @Operation(
            summary = "Get Deposit listing.",
            description = "Retrieves product listing for Deposit. [PIC: Weixian]")
    @PostMapping("/deposit/listing")
    public ResponseEntity<ApiResponse<MTDepositSearchDTO>> depositProductListing(@RequestHeader(HttpHeaders.AUTHORIZATION) String auth, @RequestBody AcctNameListReqDTO req) {
        UserAndCorp userAndCorp = jwtUtil.extractUserIdAndCorpId(auth);
        return ResponseEntity.status(HttpStatus.OK).body(mtDepositService.getDepositAccountList(userAndCorp, req));
    }

    @Operation(
            summary = "Get Investment listing.",
            description = "Retrieves product listing for Investment. [PIC: Deeba]")
    @PostMapping("/investment/listing")
    public ResponseEntity<ApiResponse<MTINVUnitTrustSearchDTO>> invProductListing(@RequestHeader(HttpHeaders.AUTHORIZATION) String auth, @RequestBody AcctNameListReqDTO req) {
        UserAndCorp userAndCorp = jwtUtil.extractUserIdAndCorpId(auth);
        return ResponseEntity.status(HttpStatus.OK).body(mtinvUnitTrustService.getINVUTAcctLists(userAndCorp, req));
    }

    @Operation(
            summary = "Get Loans/Financing listing.",
            description = "Retrieves product listing for Loans/Financing. [PIC: Minyi]")
    @PostMapping("/loan/listing")
    public ResponseEntity<ApiResponse<MTLNSearchDTO>> loanProductListing(@RequestHeader(HttpHeaders.AUTHORIZATION) String auth, @RequestBody AcctNameListReqDTO req) {
        UserAndCorp userAndCorp = jwtUtil.extractUserIdAndCorpId(auth);
        return ResponseEntity.status(HttpStatus.OK).body(mtLoanTlfService.getLoanTLFAcctList(userAndCorp, req));
    }

    @Operation(
            summary = "Get Auto Loans/Financing listing.",
            description = "Retrieves product listing for Auto Loans/Financing. [PIC: Caixin]")
    @PostMapping("/af/listing")
    public ResponseEntity<ApiResponse<MTAFSearchDTO>> afProductListing(@RequestHeader(HttpHeaders.AUTHORIZATION) String auth, @RequestBody AcctNameListReqDTO req) {
        UserAndCorp userAndCorp = jwtUtil.extractUserIdAndCorpId(auth);
        return ResponseEntity.status(HttpStatus.OK).body(mtAutoFinanceService.getAutoFinanceList(userAndCorp, req));
    }

    @Operation(
            summary = "Get Other Banks listing.",
            description = "Retrieves product listing for other banks. [PIC: Caixin]")
    @PostMapping("/other-bank/listing")
    public ResponseEntity<ApiResponse<MTOBSearchDTO>> otherBankProductListing(@RequestHeader(HttpHeaders.AUTHORIZATION) String auth, @RequestBody MTOBAccountListReqDTO req) {
        UserAndCorp userAndCorp = jwtUtil.extractUserIdAndCorpId(auth);
        return ResponseEntity.status(HttpStatus.OK).body(mtOtherBankService.getOtherBankList(userAndCorp, req));
    }

    @Operation(
            summary = "Get trades listing.",
            description = "Retrieves product listing for trades. [PIC: Weixian]")
    @PostMapping("/trade/listing")
    public ResponseEntity<ApiResponse<MTTradeSearchDTO>> tradeProductListing(@RequestHeader(HttpHeaders.AUTHORIZATION) String auth, @RequestBody TradeMaintAccountListReqDTO req) {
        UserAndCorp userAndCorp = jwtUtil.extractUserIdAndCorpId(auth);
        return ResponseEntity.status(HttpStatus.OK).body(mtTradeService.getTradeAccountList(userAndCorp, req));
    }

    @Operation(
            summary = "Get cards listing.",
            description = "Retrieves product listing for cards. [PIC: Caixin, Waiting Requirement]")
    @PostMapping("/card/listing")
    public ResponseEntity<ApiResponse<String>> cardProductListing(@RequestHeader(HttpHeaders.AUTHORIZATION) String auth) {
        ApiResponse<String> apiResponse = new ApiResponse<>();
        apiResponse.setMsgHeader(new ErrorResponse("0"));
        apiResponse.setMsgBody("This feature is on hold, pending final requirements before development can start.");
        return ResponseEntity.ok(apiResponse);
    }

    @Operation(
            summary = "Reset account name.",
            description = "Resets an account name to the system-defined default. [PIC: Minyi]")
    @PostMapping("/reset-to-default")
    public ResponseEntity<ApiResponse<String>> resetToDefault(@RequestHeader(HttpHeaders.AUTHORIZATION) String auth, @RequestBody MTResetAccctNameReqDTO request) {
        UserAndCorp userAndCorp = jwtUtil.extractUserIdAndCorpId(auth);
        return ResponseEntity.status(HttpStatus.OK).body(mtAcctNameService.resetToDefaultAcctName(userAndCorp, request));
    }

    @Operation(
            summary = "Update Account Name",
            description = "Performs a direct update to the account name. This API is specifically intended for Super Admin users. [PIC: Caixin]")
    @PostMapping("/update-acctnm")
    public ResponseEntity<ApiResponse<EditAcctNameApprovalDTO>> updateAcctNm(@RequestHeader(HttpHeaders.AUTHORIZATION) String auth, @RequestBody MTUpdateAcctNameReqDTO request) {
        UserAndCorp userAndCorp = jwtUtil.extractUserIdAndCorpId(auth);
        return ResponseEntity.status(HttpStatus.OK).body(mtAcctNameService.updateAcctNm(userAndCorp, request));
    }
}
