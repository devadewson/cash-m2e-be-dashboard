package com.m2e.dashboard.controller.inv;

import com.m2e.dashboard.auth.JwtUtil;
import com.m2e.dashboard.config.UserAndCorp;
import com.m2e.dashboard.dto.base.AccountListReq;
import com.m2e.dashboard.dto.base.DownloadDTO;
import com.m2e.dashboard.dto.base.DownloadReq;
import com.m2e.dashboard.dto.common.ApiResponse;
import com.m2e.dashboard.dto.common.SearchCollectionResponse;
import com.m2e.dashboard.dto.investment.request.*;
import com.m2e.dashboard.dto.investment.response.*;
import com.m2e.dashboard.service.inv.INVUnitTrustService;
import com.m2e.dashboard.sp.investment.request.INVUnitTrustFundStatsSpParam;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.List;

import static com.m2e.dashboard.util.DownloadUtil.setHttpHeadersForDownload;

@Tag(name = "M2UBIZM25-230 - Investment", description = "Covers Unit Trusts. PIC: Adibah")
@RestController
@Slf4j
@RequestMapping("/investment/ut/v1")
public class INVUnitTrustController  {
    private final INVUnitTrustService unitTrustService;
    private final JwtUtil jwtUtil;

    @Autowired
    public INVUnitTrustController(INVUnitTrustService unitTrustService, JwtUtil jwtUtil) {
        this.unitTrustService = unitTrustService;
        this.jwtUtil = jwtUtil;
    }

    @Operation(
            summary = "US 1 - Unit Trust stats",
            description = "Main Header for Unit Trust, Total Number of All Required"
    )
    @PostMapping("/acct-stats")
    public ResponseEntity<com.m2e.dashboard.dto.common.ApiResponse<INVUnitTrustStatsDTO>> getINVUTAccountStats(@RequestHeader(HttpHeaders.AUTHORIZATION) String auth, @RequestBody INVUnitTrustStatReqDTO req) {
        return ResponseEntity.status(HttpStatus.OK).body(unitTrustService.getINVUTAccountStats(jwtUtil.extractUserIdAndCorpId(auth),req));
    }

    @Operation(
            summary = "US 1 - List of Unit Trusts Based on Corp Account",
            description = "List of Unit Trusts Based on Corp Account, Default All Corp Account"
    )
    @PostMapping("/acct-list")
    public ResponseEntity<ApiResponse<INVUnitTrustSearchDTO>> getINVUTAccountLists(@RequestHeader(HttpHeaders.AUTHORIZATION) String auth, @RequestBody AccountListReq req) {
        UserAndCorp userAndCorp = jwtUtil.extractUserIdAndCorpId(auth);
        return ResponseEntity.status(HttpStatus.OK).body(unitTrustService.getINVUTAccountLists(userAndCorp, req));
    }
    @Operation(
            summary = "US 2, 3, 5 - Main Download Unit Trusts Files",
            description = "Main Download Unit Trusts Files"
    )
    @PostMapping(value = "/download", produces = {MediaType.APPLICATION_OCTET_STREAM_VALUE})
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(schema = @Schema(oneOf = {INVUnitTrustAccDownloadReqDTO.class, INVUnitTrustFundDownloadReqDTO.class, INVUnitTrustHistDownloadReqDTO.class}))
    )
    public ResponseEntity<byte[]> downloadINVUTAccount(@RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String auth, @RequestBody DownloadReq request) {
        UserAndCorp userAndCorp = jwtUtil.extractUserIdAndCorpId(auth);
        DownloadDTO result = unitTrustService.downloadINVUTAccount(userAndCorp, request);
        HttpHeaders headers = setHttpHeadersForDownload(result);
        return new ResponseEntity<>(result.getData(), headers, HttpStatus.OK);
    }

    @Operation(
            summary = "US 3 - Unit Trust Fund stats",
            description = "Main Header for Unit Trust Funds, Total Number of All Required"
    )
    @PostMapping("/acct-fund-stats")
    public ResponseEntity<com.m2e.dashboard.dto.common.ApiResponse<INVUnitTrustFundStatsDTO>> getINVUTFundAccountStats(@RequestHeader(HttpHeaders.AUTHORIZATION) String auth, @RequestBody INVUnitTrustFundStatsSpParam request) {
        return ResponseEntity.status(HttpStatus.OK).body(unitTrustService.getINVUTFundAccountStats(jwtUtil.extractUserIdAndCorpId(auth),request));
    }

    @Operation(
            summary = "US 3 - List of Unit Trust Funds Based on Unit Trust",
            description = "List of Unit Trusts Funds given a Unit Trust"
    )
    @PostMapping("/fund-list")
    public ResponseEntity<com.m2e.dashboard.dto.common.ApiResponse<SearchCollectionResponse<INVUnitTrustFundSearchDTO>>> getINVUTFunds(@RequestHeader(HttpHeaders.AUTHORIZATION) String auth, @RequestBody INVUnitTrustFundReqDTO unitTrustFundReqDto) {
        UserAndCorp userAndCorp = jwtUtil.extractUserIdAndCorpId(auth);
        return ResponseEntity.status(HttpStatus.OK).body(unitTrustService.getINVUTFunds(userAndCorp, unitTrustFundReqDto));
    }
    @Operation(
            summary = "US 4 - Unit Trusts Fund Details",
            description = "Unit Trusts Fund Details - Pop up Screen"
    )
    @PostMapping("/fund-dtl")
    public ResponseEntity<com.m2e.dashboard.dto.common.ApiResponse<INVUnitTrustDTO>> getINVUTFundDetails(@RequestHeader(HttpHeaders.AUTHORIZATION) String auth, @RequestBody INVUnitTrustDetailReqDTO unitTrustDetailReqDto) throws ParseException {
        UserAndCorp userAndCorp = jwtUtil.extractUserIdAndCorpId(auth);
        return ResponseEntity.status(HttpStatus.OK).body(unitTrustService.getINVUTFundDetails(userAndCorp, unitTrustDetailReqDto));
    }
    @Operation(
            summary = "US 5 - Page of Unit Trust Transaction History",
            description = "Page of Unit Trust Transaction History"
    )
    @PostMapping("/trxn-hist")
    public ResponseEntity<com.m2e.dashboard.dto.common.ApiResponse<SearchCollectionResponse<INVUnitTrustTransHistSearchDTO>>> getINVUTTransHist(@RequestHeader(HttpHeaders.AUTHORIZATION) String auth, @RequestBody INVUnitTrustHistReqDTO unitTrustHistReqDto) {
        UserAndCorp userAndCorp = jwtUtil.extractUserIdAndCorpId(auth);
        return ResponseEntity.status(HttpStatus.OK).body(unitTrustService.getINVUTTransHist(userAndCorp,unitTrustHistReqDto));
    }

    @Operation(
            summary = "Dropdown of Unit Trust Funds",
            description = "Dropdown of Unit Trust Funds for Transaction History Filter"
    )
    @PostMapping("/getFundList")
    public ResponseEntity<ApiResponse<List<INVUnitTrustFundDropdownDTO>>> getINVUTDropdownFunds(@RequestHeader(HttpHeaders.AUTHORIZATION) String auth, @RequestBody INVUnitTrustFundDropdownReqDTO fundDropdownReqDto) {
        return ResponseEntity.status(HttpStatus.OK).body(unitTrustService.getINVUTDropdownFunds(fundDropdownReqDto));
    }
}