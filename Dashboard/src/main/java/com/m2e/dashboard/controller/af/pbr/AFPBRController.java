package com.m2e.dashboard.controller.af.pbr;

import com.m2e.dashboard.dto.af.request.pbr.AFPBRAccountDetailReq;
import com.m2e.dashboard.dto.af.request.pbr.AFPBRAcctStatsReqDTO;
import com.m2e.dashboard.dto.af.request.pbr.AFPBRTrxHistoryReq;
import com.m2e.dashboard.dto.af.request.pbr.download.AFPBRAccountListingDownloadReq;
import com.m2e.dashboard.dto.af.request.pbr.download.AFPBRSubaccountListingDownloadReq;
import com.m2e.dashboard.dto.af.request.pbr.download.AFPBRTrxHistoryDownloadReq;
import com.m2e.dashboard.dto.af.response.pbr.*;
import com.m2e.dashboard.dto.base.DownloadDTO;
import com.m2e.dashboard.dto.base.DownloadReq;
import com.m2e.dashboard.dto.base.af.AFAccountListReq;
import com.m2e.dashboard.dto.af.request.pbr.AFPBRSubaccountListReq;
import com.m2e.dashboard.dto.common.ApiResponse;
import com.m2e.dashboard.service.af.pbr.AFPBRService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

import static com.m2e.dashboard.util.DownloadUtil.setHttpHeadersForDownload;

@Tag(name = "Auto Finance PBR - Payment Before Registration", description = "PIC: Kam Min Yi")
@RestController
@RequestMapping("/af/pbr/v1")
public class AFPBRController {

    @Autowired
    AFPBRService service;
    private static final Logger logger = LoggerFactory.getLogger(AFPBRController.class);

    @Operation(
            summary = "[MOCK DATA] US 4 - Get PBR main page account statistics",
            description = "Get PBR main page account statistics"
    )
    @PostMapping("/acct-stats")
    public ResponseEntity<ApiResponse<AFPBRStatisticDTO>> getAccountStatistics(@RequestHeader(value = HttpHeaders.AUTHORIZATION) String auth, @Valid @RequestBody AFPBRAcctStatsReqDTO request) {
        return ResponseEntity.ok(service.getPbrAccountStatistic(auth, request));
    }

    @Operation(
            summary = "[MOCK DATA] US 9 - Get list of accounts (account/facility view)",
            description = "Get list of accounts based on filter")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                content = @Content(mediaType = "application/json", schema = @Schema(oneOf = {AFPBRViewByAccountDTO.class, AFPBRViewByFacilityDTO.class}
            )))
    })
    @PostMapping("/acct-list")
    public ResponseEntity<ApiResponse<AFPBRAcctSummaryDTO>> getAccountListing(@RequestHeader(value = HttpHeaders.AUTHORIZATION) String auth, @Valid @RequestBody AFAccountListReq request) {
        return ResponseEntity.ok(service.getPbrAccountListing(auth, request));
    }

    @Operation(
            summary = "[MOCK DATA] US 21  - Get list of units",
            description = "Get list of units tagged under an account")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = AFPBRUnitSummaryDTO.class)))
    })
    @PostMapping("/sub-acct-list")
    public ResponseEntity<ApiResponse<AFPBRSubAcctSummaryDTO>> getSubAccountListing(@RequestHeader(value = HttpHeaders.AUTHORIZATION) String auth, @Valid @RequestBody AFPBRSubaccountListReq request) {
        return ResponseEntity.ok(service.getPbrSubaccountListing(auth, request));
    }

    @Operation(
            summary = "[MOCK DATA] US 17 & 21 - View unit details",
            description = "- Get account (facility) details\n- Get subaccount (unit) details")
    @PostMapping("/acct-dtl")
    public ResponseEntity<ApiResponse<?>> getAccountDetail(@RequestHeader(value = HttpHeaders.AUTHORIZATION) String auth, @Valid @RequestBody AFPBRAccountDetailReq request) {
        return ResponseEntity.ok(service.getPbrAccountDetail(auth, request));
    }

    @Operation(
            summary = "[MOCK DATA] Get transaction history list",
            description = "Get transaction history list")
    @PostMapping("/trxn-hist")
    public ResponseEntity<ApiResponse<AFPBRTrxHistorySummaryDTO>> getTrxnHistory(@RequestHeader(HttpHeaders.AUTHORIZATION) String auth, @Valid @RequestBody AFPBRTrxHistoryReq request) {
        return ResponseEntity.status(HttpStatus.OK).body(service.pbrTrxHistory(request, auth));
    }

    @Operation(
            summary = "[MOCK DATA] Download",
            description =
                "- Download list of accounts (view by account)\n"+
                "- Download list of accounts (view by facility)\n"+
                "- Download list of unit\n"+
                "- Download transaction history\n"
    )
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "PBR Download Requests",
            required = true,
            content = @Content(schema = @Schema(oneOf = {AFPBRAccountListingDownloadReq.class, AFPBRSubaccountListingDownloadReq.class, AFPBRTrxHistoryDownloadReq.class}))
    )
    @PostMapping(value = "/download", produces = {MediaType.APPLICATION_OCTET_STREAM_VALUE})
    public ResponseEntity<byte[]> downloadFile(@RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String auth, @RequestBody DownloadReq request) {
        DownloadDTO result = service.downloadPbrList(auth, request);
        HttpHeaders headers = setHttpHeadersForDownload(result);
        return new ResponseEntity<>(result.getData(), headers, HttpStatus.OK);
    }
}