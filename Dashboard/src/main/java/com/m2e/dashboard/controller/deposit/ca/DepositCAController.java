package com.m2e.dashboard.controller.deposit.ca;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.m2e.dashboard.auth.JwtUtil;
import com.m2e.dashboard.config.UserAndCorp;
import com.m2e.dashboard.dto.base.AccountListReq;
import com.m2e.dashboard.dto.base.DownloadDTO;
import com.m2e.dashboard.dto.base.DownloadReq;
import com.m2e.dashboard.dto.common.ApiResponse;
import com.m2e.dashboard.dto.common.SearchCollectionResponse;
import com.m2e.dashboard.dto.deposit.ca.request.*;
import com.m2e.dashboard.dto.deposit.ca.response.*;
import com.m2e.dashboard.service.deposit.ca.DepositCAService;
import com.m2e.dashboard.service.deposit.ca.MockDepositDataService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.m2e.dashboard.enums.Application.PORTFOLIO;
import static com.m2e.dashboard.enums.Product.DEPOSIT;
import static com.m2e.dashboard.enums.SubProductEnum.CURRENT_ACCOUNT;
import static com.m2e.dashboard.util.DownloadUtil.setHttpHeadersForDownload;

/**
 * No US 2
 * User Story with No Endpoints yet
 * US 12 - NOT YET
 * US 5, 14 - Function Entitlement respective to each Account - NOT YET
 * US 6 - Search within every listing endpoint - DONE
 * US 17 - Irrelevant, FE logic only - NA
 * US 18 - Customise field~ hmm - NOT YET
 * US 19 - MT103 - NOT YET
 */
@Tag(name = "M2UBIZM25-180 - Deposit CA - Current Accounts", description = "PIC: Wei Xian & Ben")
@RestController
@RequestMapping("/deposit/ca/v1")
public class DepositCAController {

    private static final Logger logger = LoggerFactory.getLogger(DepositCAController.class);

    @Autowired
    private DepositCAService depositCAService;

    @Autowired
    private MockDepositDataService mockDepositDataService;

    @Autowired
    JwtUtil jwtUtil;

    @Operation(
        summary = "US 1, 3, 4 - Get deposit CA summary list",
        description = "Get deposit CA summary list")
    @PostMapping("acct-list")
    public ResponseEntity<ApiResponse<DepositAcctListingDTO>> getAccountList(@RequestHeader(HttpHeaders.AUTHORIZATION) String auth, @RequestBody AccountListReq req) {
        UserAndCorp userAndCorp = jwtUtil.extractUserIdAndCorpId(auth);
        return ResponseEntity.ok(depositCAService.getDepositCAAccountList(userAndCorp, req));
    }

    @Operation(
        summary = "US 1, 3, 4 - Get deposit CA stats",
        description = "Get deposit CA stats"
    )
    @PostMapping("acct-stats")
    public ResponseEntity<ApiResponse<DepositAcctStatsDTO>> getAccountStats(@RequestHeader(HttpHeaders.AUTHORIZATION) String auth, @RequestBody DepositAcctStatsReqDTO req) {
        UserAndCorp userAndCorp = jwtUtil.extractUserIdAndCorpId(auth);
        return ResponseEntity.ok(depositCAService.getDepositCAAccountStats(userAndCorp, req));
    }

    @Operation(
        summary = "US 14.1 - Get deposit account EOD balance",
        description = "Get deposit account EOD balance"
    )
    @PostMapping("acct-eod-balance")
    public ResponseEntity<ApiResponse<SearchCollectionResponse<DepositCAEODBalanceDTO>>> getDepositAccountEODBalance(@RequestHeader(HttpHeaders.AUTHORIZATION) String auth, @RequestBody DepositCAEODBalanceReqDTO req) {
        UserAndCorp userAndCorp = jwtUtil.extractUserIdAndCorpId(auth);
        return ResponseEntity.ok(depositCAService.getDepositAccountEODBalance(userAndCorp, req));
    }

    @Operation(
        summary = "US 13 - Get deposit account float details",
        description = "Get deposit account float details")
    @PostMapping("acct-float-dtl")
    public ResponseEntity<ApiResponse<DepositAcctFloatDTO>> getDepositAccountFloatDetails(@RequestHeader(HttpHeaders.AUTHORIZATION) String auth, @RequestBody DepositAcctFloatReqDTO req) {
        return ResponseEntity.status(HttpStatus.OK).body(depositCAService.getDepositCAAcctFloatDetailESB(req));
    }

    @Operation(
        summary = "US 7, 8, 9, 10, 11 - Retrieve Deposit Account Detail",
        description = "Retrieve Deposit Account Detail")
    @PostMapping("/acct-dtl")
    public ResponseEntity<ApiResponse<DepositAccountDetailDTO>> retrieveDepositAccountDetail(@RequestHeader(HttpHeaders.AUTHORIZATION) String auth, @Valid @RequestBody DepositAccountDetailReqDTO request) {
        return ResponseEntity.status(HttpStatus.OK).body(depositCAService.getDepositCAAcctDetail(request));
    }

    @Operation(summary = "Deposit CA account Download")
    @PostMapping(value = "/download", produces = {MediaType.APPLICATION_OCTET_STREAM_VALUE})
    public ResponseEntity<byte[]> downloadReport(@RequestHeader(HttpHeaders.AUTHORIZATION) String auth, @RequestBody DownloadReq request) {
        UserAndCorp userAndCorp = jwtUtil.extractUserIdAndCorpId(auth);
        DownloadDTO result = depositCAService.downloadCADeposit(userAndCorp, request);
        HttpHeaders headers = setHttpHeadersForDownload(result);
        return new ResponseEntity<>(result.getData(), headers, HttpStatus.OK);
    }

    @Operation(
        summary = "US 15, 16 (Filter, Search, No download) - Retrieve Deposit Transaction History",
        description = "Retrieve Deposit Transaction History in the list")
    @PostMapping("/trxn-hist")
    public ResponseEntity<ApiResponse<DepositCATrxHistoryDTO>> getDepositCATrxHistory(@RequestHeader(HttpHeaders.AUTHORIZATION) String auth, @Valid @RequestBody DepositTrxHistoryReqDTO request) {
        UserAndCorp userAndCorp = jwtUtil.extractUserIdAndCorpId(auth);
        return ResponseEntity.status(HttpStatus.OK).body(depositCAService.getDepositCATrxHistory(userAndCorp, request));
    }

    @Operation(
            summary = "US 15.1 - Retrieve Deposit Debit Card Transaction History",
            description = "Retrieve Deposit Debit Card Transaction History")
    @PostMapping("/debit-card-txn")
    public ResponseEntity<ApiResponse<SearchCollectionResponse<DepositCADebitCardTrxDTO>>> getDepositCADebitCardTrxHist(@RequestHeader(HttpHeaders.AUTHORIZATION) String auth, @Valid @RequestBody DepositCADebitCardTrxReqDTO request) {
        UserAndCorp userAndCorp = jwtUtil.extractUserIdAndCorpId(auth);
        return ResponseEntity.status(HttpStatus.OK).body(depositCAService.getDepositCADebitCardHistoryList(userAndCorp, request));
    }

    @Operation(
        summary = "US 19, 21 - Retrieve Deposit Payment Detail",
        description = "Retrieve Deposit Payment Detail in the list")
    @PostMapping("/pymt-dtl")
    public ResponseEntity<ApiResponse<SearchCollectionResponse<DepositPaymentHistoryDTO>>> getDepositCAPaymentHistoryList(@RequestHeader(HttpHeaders.AUTHORIZATION) String auth, @Valid @RequestBody DepositPaymentHistoryReqDTO request) {
        UserAndCorp userAndCorp = jwtUtil.extractUserIdAndCorpId(auth);
        return ResponseEntity.status(HttpStatus.OK).body(depositCAService.getDepositCAPaymentHistoryList(userAndCorp, request));
    }

    @Operation(
        summary = "US 20 - Retrieve Deposit Payment Detail tab's Reference No Detail",
        description = "Retrieve Deposit Payment Detail tab's Reference No Detail")
    @PostMapping("/trxn-ref")
    public ResponseEntity<ApiResponse<DepositTrxReferenceDTO>> getDepositCAPaymentDetailTrxRef(@RequestHeader(HttpHeaders.AUTHORIZATION) String auth, @Valid @RequestBody DepositTrxReferenceReqDTO request) {
        UserAndCorp userAndCorp = jwtUtil.extractUserIdAndCorpId(auth);
        return ResponseEntity.status(HttpStatus.OK).body(depositCAService.getDepositCAPaymentDetailTrxRef(userAndCorp, request));
    }

    @Operation(
        summary = "US 19 (SWIFT GPI) - Retrieve Deposit Payment Detail tab's GPI Status",
        description = "Retrieve Deposit Payment Detail tab's GPI Status")
    @PostMapping("/view-gpi-status")
    public ResponseEntity<ApiResponse<DepositCAGPIStatusDTO>> retrieveDepositGPIStatus(@RequestHeader(HttpHeaders.AUTHORIZATION) String auth, @Valid @RequestBody DepositGPIStatusReqDTO request) throws JsonProcessingException{
        logger.info(request.toString());
        return ResponseEntity.status(HttpStatus.OK).body(depositCAService.getDepositCAGPIStatus(request));
    }

    @Operation(
            summary = "Preview Swift MT103 file before download",
            description = "Preview Swift MT103 file before download")
    @PostMapping("/preview-mt103")
    public ResponseEntity<ApiResponse<DepositCAPreviewMt103DTO>> previewMT103(@RequestHeader(HttpHeaders.AUTHORIZATION) String auth, @Valid @RequestBody DepositCAPreviewMt103ReqDTO request) throws JsonProcessingException{
        logger.info(request.toString());
        return ResponseEntity.status(HttpStatus.OK).body(depositCAService.previewMT103(request));
    }

    //TODO: TESTING MOCK FOR GPI STATUS ONLY, REMOVE AFTER TESTING DONE
    @Operation(
            summary = "US 19 (SWIFT GPI) - Retrieve Deposit Payment Detail tab's GPI Status",
            description = "Retrieve Deposit Payment Detail tab's GPI Status")
    @PostMapping("/view-gpi-status-test")
    public ResponseEntity<ApiResponse<DepositCAGPIStatusDTO>> retrieveDepositGPIStatusTest(@RequestHeader(HttpHeaders.AUTHORIZATION) String auth, @Valid @RequestBody DepositGPIStatusReqDTO request) throws JsonProcessingException{
        logger.info(request.toString());
        return ResponseEntity.status(HttpStatus.OK).body(mockDepositDataService.mockGPIStatus());
    }
}
