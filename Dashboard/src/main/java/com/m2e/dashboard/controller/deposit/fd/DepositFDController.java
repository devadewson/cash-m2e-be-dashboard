package com.m2e.dashboard.controller.deposit.fd;


import com.m2e.dashboard.auth.JwtUtil;
import com.m2e.dashboard.config.UserAndCorp;
import com.m2e.dashboard.dto.base.*;
import com.m2e.dashboard.dto.common.ApiResponse;
import com.m2e.dashboard.dto.common.SearchCollectionResponse;
import com.m2e.dashboard.dto.deposit.fd.request.*;
import com.m2e.dashboard.dto.deposit.fd.request.download.DepositFDAccountListingDownloadReqDTO;
import com.m2e.dashboard.dto.deposit.fd.request.download.DepositFDCertificateListingDownloadReqDTO;
import com.m2e.dashboard.dto.deposit.fd.request.download.DepositFDTrxHistoryDownloadReqDTO;
import com.m2e.dashboard.dto.deposit.fd.response.*;
import com.m2e.dashboard.service.deposit.fd.DepositFDService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.m2e.dashboard.util.DownloadUtil.setHttpHeadersForDownload;

/**
 * Save & Get customised transaction history field
 */
@Tag(name = "M2UBIZM25-180 - Deposit FD - Fixed Deposits", description = "PIC: Ben Wong & Kam Min Yi")
@RestController
@Slf4j
@RequestMapping("/deposit/fd/v1")
public class DepositFDController {

    @Autowired
    DepositFDService depositFDService;

    @Autowired
    JwtUtil jwtUtil;

    @Operation(
            summary = "US 35, 36, 37 - Get fixed deposit stats",
            description = "Get fixed deposit stats"
    )
        @PostMapping("acct-stats")
    public ResponseEntity<ApiResponse<DepositFDAcctStatsDTO>> getDepositFDAcctStats(@RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String auth, @RequestBody DepositFDAcctStatsReqDTO request) {
        UserAndCorp userAndCorp = jwtUtil.extractUserIdAndCorpId(auth);
        return ResponseEntity.status(HttpStatus.OK).body(depositFDService.getDepositFDAcctStats(request, userAndCorp));
    }

    @Operation(
            summary = "US 35, 36, 37 - Get fixed deposit summary list",
            description = "Get fixed deposit summary list")
    @PostMapping("acct-list")
    public ResponseEntity<ApiResponse<DepositFDAcctSummaryDTO>> getDepositFDAcctList(@RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String auth, @RequestBody AccountListReq request) {
        UserAndCorp userAndCorp = jwtUtil.extractUserIdAndCorpId(auth);
        return ResponseEntity.status(HttpStatus.OK).body(depositFDService.getDepositFDAcctList(request, userAndCorp));
    }

    @Operation(
            summary = "US 39 - Fixed Deposit Account Detail",
            description = "Fixed Deposit Account Detail")
    @PostMapping("/acct-dtl")
    public ResponseEntity<ApiResponse<DepositFDAccountDetailDTO>> getDepositFDAcctDtl(@RequestHeader(HttpHeaders.AUTHORIZATION) String auth, @Valid @RequestBody DepositFDAcctDetailReqDTO request) {
        UserAndCorp userAndCorp = jwtUtil.extractUserIdAndCorpId(auth);
        return ResponseEntity.status(HttpStatus.OK).body(depositFDService.getDepositFDAcctDtl(request, userAndCorp));
    }

    @Operation(
            summary = "US 40 - Retrieve Fixed Deposit Certificates List",
            description = "Retrieve Fixed Deposit Certificate in the list")
    @PostMapping("/cert-list")
    public ResponseEntity<ApiResponse<DepositFDCertificateListDTO>> getDepositFDAcctCertList(@RequestHeader(HttpHeaders.AUTHORIZATION) String auth, @Valid @RequestBody DepositFDAcctCertificatesReqDTO request) {
        UserAndCorp userAndCorp = jwtUtil.extractUserIdAndCorpId(auth);
        return ResponseEntity.status(HttpStatus.OK).body(depositFDService.getDepositFDAcctCertDtlList(request, userAndCorp));
    }

    @Operation(
            summary = "US 41 - Retrieve Fixed Deposit Transaction History",
            description = "Retrieve Fixed Deposit Transaction History in the list")
    @PostMapping("/trxn-hist")
    public ResponseEntity<ApiResponse<SearchCollectionResponse<DepositFDTrxHistoryDTO>>> getDepositFDTrxHistory(@RequestHeader(HttpHeaders.AUTHORIZATION) String auth, @Valid @RequestBody DepositFDTrxHistoryReqDTO request) {
        UserAndCorp userAndCorp = jwtUtil.extractUserIdAndCorpId(auth);
        return ResponseEntity.status(HttpStatus.OK).body(depositFDService.getDepositFDTrxHistory(request, userAndCorp));
    }

    @Operation(
            summary = "Fixed Deposit account Download",
            description =
                    "- Download list of accounts\n"+
                    "- Download list of certificates\n"+
                    "- Download account transaction history\n"
    )
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "FD Download Requests",
            required = true,
            content = @Content(schema = @Schema(oneOf = {DepositFDTrxHistoryDownloadReqDTO.class, DepositFDCertificateListingDownloadReqDTO.class, DepositFDAccountListingDownloadReqDTO.class}))
    )
    @PostMapping(value = "/download", produces = {MediaType.APPLICATION_OCTET_STREAM_VALUE})
    public ResponseEntity<byte[]> downloadFile(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String auth,
            @RequestBody DownloadReq request) {
        UserAndCorp userAndCorp = jwtUtil.extractUserIdAndCorpId(auth);
        DownloadDTO result = depositFDService.downloadFdList(request, userAndCorp);

        HttpHeaders headers = setHttpHeadersForDownload(result);
        return new ResponseEntity<>(result.getData(), headers, HttpStatus.OK);
    }

    @Operation(
            summary = "Get Make A Placement entitlement (non-account specific)"
    )
    @PostMapping("make-a-placement")
    public ResponseEntity<ApiResponse<DepositFDMakeAPlacementDTO>> getDepositFDMakeAPlacement(@RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String auth) {
        UserAndCorp userAndCorp = jwtUtil.extractUserIdAndCorpId(auth);
        return ResponseEntity.status(HttpStatus.OK).body(depositFDService.getDepositFDMakeAPlacement(userAndCorp));
    }
}
