package com.m2e.dashboard.controller.overview;


import com.m2e.dashboard.auth.JwtUtil;
import com.m2e.dashboard.config.UserAndCorp;
import com.m2e.dashboard.dto.common.ApiResponse;
import com.m2e.dashboard.dto.overview.request.AssetLiabilitiesGraphReqDTO;
import com.m2e.dashboard.dto.overview.request.AssetLiabilitiesReqDTO;
import com.m2e.dashboard.dto.overview.request.OverviewReqDTO;
import com.m2e.dashboard.dto.overview.response.AssetLiabilitiesGraphResDTO;
import com.m2e.dashboard.dto.overview.response.AssetLiabilitiesResDTO;
import com.m2e.dashboard.dto.overview.response.OverviewResDTO;
import com.m2e.dashboard.service.overview.AssetGraph.AssetGraphService;
import com.m2e.dashboard.service.overview.Refactor.DepositOverviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Deposit Overview", description = "PIC: Deva")
@RestController
@Slf4j
@RequestMapping("/portfolio/deposit/v1")
public class DepositOverviewController {

    @Autowired
    JwtUtil jwtUtil;

    @Autowired
    DepositOverviewService depositOverviewService;

    @Autowired
    AssetGraphService assetGraphService;

    @Operation(
            summary = "Deposit - All Deposits Overview",
            description = "Get all deposit products combined"
    )
    @PostMapping("/overview")
    public ResponseEntity<?> getDepositOverview(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String auth,
            @RequestBody OverviewReqDTO request) {

        log.info("REST request - Deposit Overview");

        UserAndCorp userAndCorp = jwtUtil.extractUserIdAndCorpId(auth);

        OverviewResDTO result = depositOverviewService.getDepositOverview(
                userAndCorp,
                request.getBaseCurrency(),
                request.getCorpCifNo());

        return ResponseEntity.status(HttpStatus.OK)
                .body(new com.m2e.dashboard.dto.common.ApiResponse<>(
                        new com.m2e.dashboard.dto.common.ErrorResponse("0"),
                        result));
    }

    @Operation(
            summary = "Deposit - Asset & Liabilities Summary",
            description = "Get total assets and liabilities for deposit products only (realtime from BTS)"
    )
    @PostMapping("/asset")
    public ResponseEntity<?> getDepositAssetLiabilities(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String auth,
            @RequestBody AssetLiabilitiesReqDTO request) {

        log.info("REST request - Deposit Asset & Liabilities | CIF={}", request.getCorpCifNo());

        UserAndCorp userAndCorp = jwtUtil.extractUserIdAndCorpId(auth);

        ApiResponse<AssetLiabilitiesResDTO> response = depositOverviewService.getDepositAssetLiabilities(
                userAndCorp,
                request.getBaseCurrency(),
                request.getCorpCifNo());

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(
            summary = "US 1 - Portfolio Overview Graph",
            description = "Asset & Liabilities graph dari snapshot harian. "
                    + "Data disimpan dalam IDR, dikonversi ke baseCurrency saat dibaca."
    )
    @PostMapping("/asset-graph")
    public ResponseEntity<?> getAssetLiabilitiesGraph(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String auth,
            @RequestBody AssetLiabilitiesGraphReqDTO request) {

        log.info("REST request - Asset Graph | interval={} | currency={}",
                request.getInterval(), request.getBaseCurrency());

        UserAndCorp userAndCorp = jwtUtil.extractUserIdAndCorpId(auth);

        ApiResponse<AssetLiabilitiesGraphResDTO> response =
                assetGraphService.getAssetGraph(userAndCorp, request);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
