package com.m2e.dashboard.controller.loan.tlf;

import com.m2e.dashboard.dto.base.AccountStatisticDTO;
import com.m2e.dashboard.dto.base.DownloadDTO;
import com.m2e.dashboard.dto.base.DownloadReq;
import com.m2e.dashboard.dto.base.loan.LNAcctListReq;
import com.m2e.dashboard.dto.base.loan.LNReq;
import com.m2e.dashboard.dto.common.ApiResponse;
import com.m2e.dashboard.dto.loan.tlf.request.LNTLFDetailReqDTO;
import com.m2e.dashboard.dto.loan.tlf.request.LNTLFTranchesDetailReqDTO;
import com.m2e.dashboard.dto.loan.tlf.request.LNTLFTranchesListReqDTO;
import com.m2e.dashboard.dto.loan.tlf.request.LNTrxHistoryReqDTO;
import com.m2e.dashboard.dto.loan.tlf.response.*;
import com.m2e.dashboard.dto.loan.tlf.response.acctdetail.*;
import com.m2e.dashboard.service.loan.tlf.LNTLFService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.m2e.dashboard.util.DownloadUtil.setHttpHeadersForDownload;

@Tag(name = "M2UBIZM25-976 Loan - Term Loans/Financing", description = "PIC: Merlin")
@RestController
@Slf4j
@RequestMapping("/loan/tlf/v1")
public class LNTLFController {
    @Autowired
    private LNTLFService service;

    @Operation(summary = "US 3, 4 [TOP] - Loan Term Loan Financing Account Statistic")
    @PostMapping("/acct-stats")
    public ResponseEntity<ApiResponse<AccountStatisticDTO>> getLNTLFAccountStatistic(@RequestHeader(HttpHeaders.AUTHORIZATION) String auth, @RequestBody LNReq request) {
        return ResponseEntity.status(HttpStatus.OK).body(service.getLNTLFAccountStatistic(auth, request));
    }

    @Operation(summary = "US 3, 4 [BOTTOM] - Loan Term Loan Financing Account Listing")
    @PostMapping("/acct-list")
    public ResponseEntity<ApiResponse<LNTLFAcctListingDTO>> getTLFAccountListing(@RequestHeader(HttpHeaders.AUTHORIZATION) String auth, @RequestBody LNAcctListReq request) {
        return ResponseEntity.status(HttpStatus.OK).body(service.getTLFAccountListing(auth, request));
    }

    @Operation(summary = "KIV - Download Loan Term Loan Financing")
    @PostMapping(value = "/download", produces = {MediaType.APPLICATION_OCTET_STREAM_VALUE})
    public ResponseEntity<byte[]> downloadTLF(@RequestHeader(HttpHeaders.AUTHORIZATION) String auth, @RequestBody DownloadReq request) {
        DownloadDTO result = service.downloadTLF(auth, request);
        HttpHeaders headers = setHttpHeadersForDownload(result);
        return new ResponseEntity<>(result.getData(), headers, HttpStatus.OK);
    }

    @Operation(summary = "US 5 [TOP] - Loan Term Loan Financing Details",
            description = "FE devs, please take note on multiple response schemas reflecting different format details for different term loan/financing types")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            content = @Content(mediaType = "application/json", schema = @Schema(oneOf = {
                    LNTLFDetailConventionalDTO.class,
                    LNTLFDetailIslamicDTO.class,
                    LNTLFDetailMYForeignConventionalDTO.class,
                    LNTLFDetailMYForeignIslamicDTO.class,
                    LNTLFDetailSGConventionalFITASDTO.class,
                    LNTLFDetailSGIslamicFITASDTO.class,
                    LNTLFDetailSGSIBSDTO.class
            })))
    })
    @PostMapping("/acct-dtl")
    public ResponseEntity<ApiResponse<LNTLFDetailDTO>> getTLFAccountDetails(@RequestHeader(HttpHeaders.AUTHORIZATION) String auth, @RequestBody LNTLFDetailReqDTO request) {
        return ResponseEntity.status(HttpStatus.OK).body(service.getTLFAccountDetail(auth, request));
    }

    @Operation(summary = "US 5 [BOTTOM] - Loan Term Loan Financing Transaction History per Account")
    @PostMapping("/trxn-hist")
    public ResponseEntity<ApiResponse<LNTLFTrxHistoryResponseDTO>> getTLFTrxHistoriesPerAccount(@RequestHeader(HttpHeaders.AUTHORIZATION) String auth, @RequestBody LNTrxHistoryReqDTO request) {
        return ResponseEntity.status(HttpStatus.OK).body(service.getTrxHistory(request, auth));
    }

    @Operation(summary = "US 5 [MIDDLE] - Loan - Term Loan Financing Tranches per Account")
    @PostMapping("/tranches")
    public ResponseEntity<ApiResponse<LNTLFTrancheListDTO>> getTLFTranchesPerAccount(@RequestHeader(HttpHeaders.AUTHORIZATION) String auth, @RequestBody LNTLFTranchesListReqDTO request) {
        return ResponseEntity.status(HttpStatus.OK).body(service.getTranches(request));
    }

    @Operation(summary = "US 5 [POPUP] - Loan Term Loan Financing Tranches Details")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(oneOf = {
                                    LNTLFTranchesDetailConventionalDTO.class,
                                    LNTLFTranchesDetailIslamicDTO.class})))
    })
    @PostMapping("/tranch-dtl")
    public ResponseEntity<ApiResponse<?>> getTLFTranchesDetails(@RequestHeader(HttpHeaders.AUTHORIZATION) String auth, @RequestBody LNTLFTranchesDetailReqDTO request) {
        return ResponseEntity.status(HttpStatus.OK).body(service.getTLFTranchesDetail(request));
    }

}