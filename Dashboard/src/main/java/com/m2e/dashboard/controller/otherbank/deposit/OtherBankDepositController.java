package com.m2e.dashboard.controller.otherbank.deposit;

import com.m2e.dashboard.auth.JwtUtil;
import com.m2e.dashboard.config.UserAndCorp;
import com.m2e.dashboard.dto.base.DownloadDTO;
import com.m2e.dashboard.dto.base.DownloadReq;
import com.m2e.dashboard.dto.common.ApiResponse;
import com.m2e.dashboard.dto.common.SearchCollectionResponse;
import com.m2e.dashboard.dto.otherbank.request.deposit.OtherBankDepositAcctDetailReqDTO;
import com.m2e.dashboard.dto.otherbank.request.deposit.OtherBankDepositAcctListingReqDTO;
import com.m2e.dashboard.dto.otherbank.request.deposit.OtherBankDepositAcctStatsReqDTO;
import com.m2e.dashboard.dto.otherbank.request.deposit.OtherBankDepositTrxHistoryReqDTO;
import com.m2e.dashboard.dto.otherbank.response.common.OtherBankAcctDetailNormalDTO;
import com.m2e.dashboard.dto.otherbank.response.deposit.OtherBankDepositAcctListingDTO;
import com.m2e.dashboard.dto.otherbank.response.deposit.OtherBankDepositAcctStatsDTO;
import com.m2e.dashboard.dto.otherbank.response.deposit.OtherBankDepositAcctStatsHistDTO;
import com.m2e.dashboard.dto.otherbank.response.deposit.OtherBankDepositTrxHistoryDTO;
import com.m2e.dashboard.service.otherbank.deposit.OtherBankDepositService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.m2e.dashboard.util.DownloadUtil.setHttpHeadersForDownload;


@Tag(name = "Other Bank – Deposit", description = "PIC: Wei Xian")
@RestController
@Slf4j
@RequestMapping("/other-bank/deposit/v1")
public class OtherBankDepositController {

    private static final Logger logger = LoggerFactory.getLogger(OtherBankDepositController.class);

    @Autowired
    private OtherBankDepositService otherBankDepositService;

    @Autowired
    JwtUtil jwtUtil;

    @Operation(
            summary = "Other bank - deposit acct list",
            description = "Other bank - deposit acct list")
    @PostMapping("acct-list")
    public ResponseEntity<ApiResponse<OtherBankDepositAcctListingDTO>> getAccountList(@RequestHeader(HttpHeaders.AUTHORIZATION) String auth, @RequestBody OtherBankDepositAcctListingReqDTO req) {
        UserAndCorp userAndCorp = jwtUtil.extractUserIdAndCorpId(auth);
        return ResponseEntity.ok(otherBankDepositService.getOtherBankDepositAccountList(userAndCorp, req));
    }

    @Operation(
            summary = "Other bank - deposit acct stats",
            description = "Other bank - deposit acct stats"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    content = @Content(mediaType = "*/*", schema = @Schema(oneOf = {
                            OtherBankDepositAcctStatsHistDTO.class,
                            OtherBankDepositAcctStatsDTO.class})))
    })
    @PostMapping("acct-stats")
    public <T> ResponseEntity<ApiResponse<T>> getAccountStats(@RequestHeader(HttpHeaders.AUTHORIZATION) String auth, @RequestBody OtherBankDepositAcctStatsReqDTO req) {
        UserAndCorp userAndCorp = jwtUtil.extractUserIdAndCorpId(auth);
        return ResponseEntity.ok(otherBankDepositService.getOtherBankDepositAccountStat(userAndCorp, req));
    }

    @Operation(
            summary = "Other bank - deposit acct details",
            description = "Other bank - deposit acct details")
    @PostMapping("/acct-dtl")
    public <T> ResponseEntity<ApiResponse<T>> getOtherBankDepositAccountDetail(@RequestHeader(HttpHeaders.AUTHORIZATION) String auth, @Valid @RequestBody OtherBankDepositAcctDetailReqDTO request) {
        UserAndCorp userAndCorp = jwtUtil.extractUserIdAndCorpId(auth);
        return ResponseEntity.status(HttpStatus.OK).body(otherBankDepositService.otherBankDepositAccountDetail(userAndCorp, request));
    }

    @Operation(
            summary = "Other bank - deposit download",
            description = "Other bank - deposit download")
    @PostMapping(value = "/download", produces = {MediaType.APPLICATION_OCTET_STREAM_VALUE})
    public ResponseEntity<byte[]> downloadReport(@RequestHeader(HttpHeaders.AUTHORIZATION) String auth, @RequestBody DownloadReq request) {
        UserAndCorp userAndCorp = jwtUtil.extractUserIdAndCorpId(auth);
        DownloadDTO result = otherBankDepositService.downloadOtherBankDeposit(userAndCorp, request);
        HttpHeaders headers = setHttpHeadersForDownload(result);
        return new ResponseEntity<>(result.getData(), headers, HttpStatus.OK);
    }

    @Operation(
            summary = "Other bank - deposit acct trx history",
            description = "Other bank - deposit acct trx history")
    @PostMapping("/trxn-hist")
    public ResponseEntity<ApiResponse<SearchCollectionResponse<OtherBankDepositTrxHistoryDTO>>> getOtherBankDepositAccountTrxHistory(@RequestHeader(HttpHeaders.AUTHORIZATION) String auth, @Valid @RequestBody OtherBankDepositTrxHistoryReqDTO request) {
        UserAndCorp userAndCorp = jwtUtil.extractUserIdAndCorpId(auth);
        return ResponseEntity.status(HttpStatus.OK).body(otherBankDepositService.otherBankDepositTrxHistory(userAndCorp, request));
    }
}
