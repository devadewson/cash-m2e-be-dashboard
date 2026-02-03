package com.m2e.dashboard.controller.otherbank.loan;

import com.m2e.dashboard.config.UserAndCorp;
import com.m2e.dashboard.dto.base.DownloadDTO;
import com.m2e.dashboard.dto.base.DownloadReq;
import com.m2e.dashboard.dto.common.ApiResponse;
import com.m2e.dashboard.dto.common.SearchCollectionResponse;
import com.m2e.dashboard.dto.otherbank.request.loan.OtherBankLoanAcctDetailReqDTO;
import com.m2e.dashboard.dto.otherbank.request.loan.OtherBankLoanAcctListReqDTO;
import com.m2e.dashboard.dto.otherbank.request.loan.OtherBankLoanAcctStatsReqDTO;
import com.m2e.dashboard.dto.otherbank.request.loan.OtherBankLoanTrxHistReqDTO;
import com.m2e.dashboard.dto.otherbank.response.common.OtherBankAcctDetailNormalDTO;
import com.m2e.dashboard.dto.otherbank.response.common.OtherBankAcctDetailSpecialListDTO;
import com.m2e.dashboard.dto.otherbank.response.deposit.OtherBankDepositAcctStatsDTO;
import com.m2e.dashboard.dto.otherbank.response.deposit.OtherBankDepositAcctStatsHistDTO;
import com.m2e.dashboard.dto.otherbank.response.loan.*;
import com.m2e.dashboard.service.otherbank.loan.OtherBankLoanService;
import com.m2e.dashboard.auth.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.m2e.dashboard.util.DownloadUtil.setHttpHeadersForDownload;

@Tag(name = "Other Bank – Loan", description = "PIC: Benjamin Wong")
@RestController
@Slf4j
@RequestMapping("/other-bank/loan/v1")
public class OtherBankLoanController {

    @Autowired
    JwtUtil jwtUtil;

    @Autowired
    OtherBankLoanService otherBankLoanService;

    @Operation(
            summary = "User Story 1 - 1.13 - Get Other Bank Loan stats",
            description = "Get Other Bank Loan stats"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    content = @Content(mediaType = "*/*", schema = @Schema(oneOf = {
                            OtherBankLoanAcctStatsHistDTO.class,
                            OtherBankLoanAcctStatsDTO.class})))
    })
    @PostMapping("acct-stats")
    public <T> ResponseEntity<ApiResponse<T>> getAccountStatistics(@RequestHeader(value = HttpHeaders.AUTHORIZATION) String auth, @RequestBody OtherBankLoanAcctStatsReqDTO request) {
        UserAndCorp userAndCorp = jwtUtil.extractUserIdAndCorpId(auth);
        return ResponseEntity.status(HttpStatus.OK).body(otherBankLoanService.otherBankLoanAccountStats(request, userAndCorp));
    }

    @Operation(
            summary = "User Story 1 - 1.13 - Get Other Bank Account list",
            description = "Get Other Bank Account list")
    @PostMapping("acct-list")
    public ResponseEntity<ApiResponse<OtherBankLoanAcctListDTO>> getAccountListing(@RequestHeader(value = HttpHeaders.AUTHORIZATION) String auth, @RequestBody OtherBankLoanAcctListReqDTO req) {
        UserAndCorp userAndCorp = jwtUtil.extractUserIdAndCorpId(auth);
        return ResponseEntity.status(HttpStatus.OK).body(otherBankLoanService.otherBankLoanAcctList(userAndCorp, req));
    }

    @Operation(
            summary = "User Story 1 - 1.13 - Retrieve Other Bank Account Detail",
            description = "Retrieve Other Bank Account Detail")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    content = @Content(mediaType = "application/json", schema = @Schema(oneOf = {OtherBankAcctDetailNormalDTO.class, OtherBankAcctDetailSpecialListDTO.class})))
    })
    @PostMapping("/acct-dtl")
    public <T> ResponseEntity<ApiResponse<T>> getAccountDetail(@RequestHeader(value = HttpHeaders.AUTHORIZATION) String auth, @Valid @RequestBody OtherBankLoanAcctDetailReqDTO request) {
        UserAndCorp userAndCorp = jwtUtil.extractUserIdAndCorpId(auth);
        return ResponseEntity.status(HttpStatus.OK).body(otherBankLoanService.otherBankLoanAcctDetail(userAndCorp, request));
    }

    @Operation(
            summary = "User Story 1 - 1.13 - Retrieve Other Bank Trx History List",
            description = "Retrieve Other Bank Trx History in the list")
    @PostMapping("/trxn-hist")
    public ResponseEntity<ApiResponse<SearchCollectionResponse<OtherBankLoanTrxHistDTO>>> getTrxnHistory(@RequestHeader(value = HttpHeaders.AUTHORIZATION) String auth, @Valid @RequestBody OtherBankLoanTrxHistReqDTO request) {
        UserAndCorp userAndCorp = jwtUtil.extractUserIdAndCorpId(auth);
        return ResponseEntity.status(HttpStatus.OK).body(otherBankLoanService.otherBankLoanTrxHistory(userAndCorp, request));
    }

    @Operation(
            summary = "User Story 1 - 1.13 - Download feature",
            description = "Download feature"
    )
    @PostMapping("download")
    public ResponseEntity<byte[]> downloadFile(@RequestHeader(value = HttpHeaders.AUTHORIZATION) String auth, @RequestBody DownloadReq request) {
        UserAndCorp userAndCorp = jwtUtil.extractUserIdAndCorpId(auth);
        DownloadDTO result = otherBankLoanService.downloadFile(userAndCorp, request);
        HttpHeaders headers = setHttpHeadersForDownload(result);
        return new ResponseEntity<>(result.getData(), headers, HttpStatus.OK);
    }
}
