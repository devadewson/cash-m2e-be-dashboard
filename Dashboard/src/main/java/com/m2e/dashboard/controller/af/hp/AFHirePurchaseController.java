package com.m2e.dashboard.controller.af.hp;

import com.m2e.dashboard.auth.JwtUtil;
import com.m2e.dashboard.config.UserAndCorp;
import com.m2e.dashboard.dto.af.request.hp.AFHPAcctDetailReqDTO;
import com.m2e.dashboard.dto.af.request.hp.AFHPAcctStatsReqDTO;
import com.m2e.dashboard.dto.af.request.hp.AFHPTrxHistReqDTO;
import com.m2e.dashboard.dto.af.response.hp.*;
import com.m2e.dashboard.dto.base.DownloadDTO;
import com.m2e.dashboard.dto.base.DownloadReq;
import com.m2e.dashboard.dto.base.af.AFAccountListReq;
import com.m2e.dashboard.dto.common.ApiResponse;
import com.m2e.dashboard.service.af.hp.AFHirePurchaseService;
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

import static com.m2e.dashboard.enums.Application.PORTFOLIO;
import static com.m2e.dashboard.enums.Product.AUTO_FINANCE;
import static com.m2e.dashboard.enums.SubProductEnum.HIRE_PURCHASE;
import static com.m2e.dashboard.util.DownloadUtil.setHttpHeadersForDownload;

@Tag(name = "Auto Finance HP/BHP - Hire Purchase/Business Hire Purchase", description = "PIC: Benjamin Wong")
@RestController
@Slf4j
@RequestMapping("/af/hp/v1")
public class AFHirePurchaseController {

  @Autowired
  AFHirePurchaseService afHirePurchaseService;

  @Autowired
  JwtUtil jwtUtil;


  @Operation(
          summary = "User Story 1 - 1.13 - Get hire purchase stats",
          description = "Get hire purchase stats"
  )
  @PostMapping("acct-stats")
  public ResponseEntity<ApiResponse<AFHPAcctStatsDTO>> getAccountStatistics(@RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String auth, @RequestBody AFHPAcctStatsReqDTO req) {
    UserAndCorp userAndCorp = jwtUtil.extractUserIdAndCorpId(auth);
    return ResponseEntity.status(HttpStatus.OK).body(afHirePurchaseService.hirePurchaseAccountStats(userAndCorp, req));
  }

  @Operation(
          summary = "User Story 1 - 1.13 - Get hire purchase account list",
          description = "Get hire purchase account list")
  @PostMapping("acct-list")
  public ResponseEntity<ApiResponse<AFHPAcctSummaryResp>> getAccountListing(@RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String auth, @RequestBody AFAccountListReq req) {
    UserAndCorp userAndCorp = jwtUtil.extractUserIdAndCorpId(auth);
    return ResponseEntity.status(HttpStatus.OK).body(afHirePurchaseService.hirePurchaseAccountListing(req, userAndCorp));
  }

  @Operation(
          summary = "User Story 1 - 1.13 - Retrieve Hire Purchase Account Detail",
          description = "Retrieve Hire Purchase Account Detail")
  @ApiResponses(value = {
          @io.swagger.v3.oas.annotations.responses.ApiResponse(
                  content = @Content(mediaType = "application/json", schema = @Schema(oneOf = {AFHPMasterAcctDetailDTO.class, AFHPSGAcctDetailDTO.class, AFHPAcctDetailDTO.class})))
  })
  @PostMapping("/acct-dtl")
  public ResponseEntity<ApiResponse<?>> getAccountDetail(@RequestHeader(HttpHeaders.AUTHORIZATION) String auth, @Valid @RequestBody AFHPAcctDetailReqDTO request) {
    UserAndCorp userAndCorp = jwtUtil.extractUserIdAndCorpId(auth);
    return ResponseEntity.status(HttpStatus.OK).body(afHirePurchaseService.hirePurchaseAcctDetail(userAndCorp, request));
  }

  @Operation(
          summary = "User Story 1 - 1.13 - Retrieve Hire Purchase Trx History List",
          description = "Retrieve Hire Purchase Trx History in the list"
  )
  @PostMapping("/trxn-hist")
  public ResponseEntity<ApiResponse<?>> getTrxnHistory(@RequestHeader(HttpHeaders.AUTHORIZATION) String auth, @Valid @RequestBody AFHPTrxHistReqDTO request) {
    UserAndCorp userAndCorp = jwtUtil.extractUserIdAndCorpId(auth);
    return ResponseEntity.status(HttpStatus.OK).body(afHirePurchaseService.hirePurchaseTrxHistory(request, userAndCorp));
  }

  @Operation(
          summary = "User Story 1 - 1.13 - Download feature",
          description = "Download feature"
  )
  @PostMapping("download")
  public ResponseEntity<byte[]> downloadFile(@RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String auth, @RequestBody DownloadReq request) {
    UserAndCorp userAndCorp = jwtUtil.extractUserIdAndCorpId(auth);
    DownloadDTO result = afHirePurchaseService.downloadHirePurchase(request, userAndCorp);
    HttpHeaders headers = setHttpHeadersForDownload(result);
    return new ResponseEntity<>(result.getData(), headers, HttpStatus.OK);
  }
}