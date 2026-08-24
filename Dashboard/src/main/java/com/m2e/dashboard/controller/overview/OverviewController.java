package com.m2e.dashboard.controller.overview;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.m2e.dashboard.auth.JwtUtil;
import com.m2e.dashboard.config.UserAndCorp;
import com.m2e.dashboard.dto.base.DownloadDTO;
import com.m2e.dashboard.dto.base.DownloadReq;
import com.m2e.dashboard.dto.overview.request.AssetLiabilitiesGraphReqDTO;
import com.m2e.dashboard.dto.overview.request.AssetLiabilitiesReqDTO;
import com.m2e.dashboard.dto.overview.request.OverviewDownloadReqDTO;
import com.m2e.dashboard.dto.overview.request.OverviewReqDTO;
import com.m2e.dashboard.dto.overview.response.OverviewResDTO;
import com.m2e.dashboard.service.overview.AssetGraph.AssetGraphService;
import com.m2e.dashboard.service.overview.OverviewService;
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

import static com.m2e.dashboard.util.DownloadUtil.setHttpHeadersForDownload;

@Tag(name = "M2UBIZM25-922 - Portfolio Overview", description = "PIC: Adibah")
@RestController
@Slf4j
@RequestMapping("/portfolio/ov/v1")
public class OverviewController {
    @Autowired
    JwtUtil jwtUtil;
    @Autowired
    AssetGraphService assetGraphService;

    @Autowired
    OverviewService overviewService;

    @Operation(
            summary = "US 1 - Portfolio Overview",
            description = "Total Asset and Liabilities"
    )
    @PostMapping("/asset")
    public ResponseEntity<?> getAssetLiabilities(@RequestHeader(HttpHeaders.AUTHORIZATION) String auth, @RequestBody AssetLiabilitiesReqDTO assetLiabilitiesReqDto) throws JsonProcessingException {
        UserAndCorp userAndCorp = jwtUtil.extractUserIdAndCorpId(auth);
        return ResponseEntity.status(HttpStatus.OK).body(overviewService.getAssetLiabilities(auth, userAndCorp, assetLiabilitiesReqDto));
    }

    @Operation(summary = "US 1 - Portfolio Overview Graph", description = "Asset and Liabilities Graph")
    @PostMapping("/asset-graph")
    public ResponseEntity<?> getAssetLiabilitiesGraph(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String auth,
            @RequestBody AssetLiabilitiesGraphReqDTO request) {

        UserAndCorp userAndCorp = jwtUtil.extractUserIdAndCorpId(auth);
        return ResponseEntity.status(HttpStatus.OK)
                .body(assetGraphService.getAssetGraph(userAndCorp, request));
    }
    @Operation(
            summary = "US 2 - Main Download Portfolio Overview Files",
            description = "Main Download Portfolio Overview Files"
    )
    @PostMapping(value = "/download", produces = {MediaType.APPLICATION_OCTET_STREAM_VALUE})
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(schema = @Schema(oneOf = {OverviewDownloadReqDTO.class}))
    )
    public ResponseEntity<byte[]> downloadPortfolioOverview(@RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String auth, @RequestBody DownloadReq request) throws JsonProcessingException {
        UserAndCorp userAndCorp = jwtUtil.extractUserIdAndCorpId(auth);
        DownloadDTO result = overviewService.downloadOverview(auth, userAndCorp, request);
        HttpHeaders headers = setHttpHeadersForDownload(result);
        return new ResponseEntity<>(result.getData(), headers, HttpStatus.OK);
    }

    @Operation(
            summary = "Get Overview",
            description = "Get Overview",
            responses = {@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "OK", content =
                    {@Content(mediaType = "application/json", schema =
                    @Schema(implementation = OverviewResDTO.class))})}
    )
    @PostMapping("/overview")
    public ResponseEntity<?> getProductOverview(@RequestHeader(HttpHeaders.AUTHORIZATION) String auth, @RequestBody OverviewReqDTO overviewReqDto) {
        UserAndCorp userAndCorp = jwtUtil.extractUserIdAndCorpId(auth);
        return ResponseEntity.status(HttpStatus.OK).body(overviewService.getProductOverview(userAndCorp, overviewReqDto));
    }

}
