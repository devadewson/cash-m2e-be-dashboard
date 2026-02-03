package com.m2e.dashboard.controller.af.fs;

import com.m2e.dashboard.auth.JwtUtil;
import com.m2e.dashboard.config.UserAndCorp;
import com.m2e.dashboard.dto.af.request.fs.*;
import com.m2e.dashboard.dto.af.response.fs.*;
import com.m2e.dashboard.dto.base.DownloadDTO;
import com.m2e.dashboard.dto.base.DownloadReq;
import com.m2e.dashboard.dto.base.af.AFAccountListReq;
import com.m2e.dashboard.dto.common.ApiResponse;
import com.m2e.dashboard.dto.common.SearchCollectionResponse;
import com.m2e.dashboard.enums.FilterAutoFinanceAccountListingEnum;
import com.m2e.dashboard.service.af.fs.AFFSService;
import com.m2e.dashboard.sp.af.fs.response.AFFSAcctListSpDTO;
import com.m2e.dashboard.sp.af.fs.response.AFFSCertListSpDTO;
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
import static com.m2e.dashboard.enums.SubProductEnum.FLOOR_STOCK;
import static com.m2e.dashboard.util.DownloadUtil.setHttpHeadersForDownload;

@Tag(name = "Auto Finance FS - Floor Stocking", description = "PIC: Benjamin Wong")
@RestController
@Slf4j
@RequestMapping("/af/fs/v1")
public class AFFloorStockingController {

  @Autowired
  JwtUtil jwtUtil;

  @Autowired
  AFFSService AFFSService;

  @Operation(
          summary = "User Story 2 - 2.13 - Get floor stocking stats",
          description = "Get floor stocking stats"
  )
  @ApiResponses(value = {
          @io.swagger.v3.oas.annotations.responses.ApiResponse(
                  content = @Content(mediaType = "application/json", schema = @Schema(oneOf = {AFFSAcctStatViewAcctDTO.class, AFFSAcctStatsViewFacilityDTO.class})))
  })
  @PostMapping("acct-stats")
  public ResponseEntity<ApiResponse<AFFSAcctStatsDTO>> getAccountStatistics(@RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String auth, @RequestBody AFFSAcctStatsReqDTO req) {
    UserAndCorp userAndCorp = jwtUtil.extractUserIdAndCorpId(auth);
    return ResponseEntity.status(HttpStatus.OK).body(AFFSService.floorStockingAccountStats(req, userAndCorp));
  }

  @Operation(
          summary = "User Story 2 - 2.13 - Get floor stocking summary list",
          description = "Get floor stocking summary list")
  @PostMapping("acct-list")
  @ApiResponses(value = {
          @io.swagger.v3.oas.annotations.responses.ApiResponse(
                  content = @Content(mediaType = "application/json", schema = @Schema(oneOf = {AFFSCertListSpDTO.class, AFFSAcctListSpDTO.class})))
  })
  public ResponseEntity<ApiResponse<AFFSAcctListViewSummaryResp<Object>>> getAccountListing(@RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String auth, @Valid @RequestBody AFAccountListReq req) {
    UserAndCorp userAndCorp = jwtUtil.extractUserIdAndCorpId(auth);
    if (req.getViewByIndicator() == FilterAutoFinanceAccountListingEnum.FACILITY) {
      ApiResponse<AFFSAcctListViewSummaryResp<Object>> facilityViewListResp = AFFSService.floorStockingAccountListingByFacility(userAndCorp, req);
      return ResponseEntity.status(HttpStatus.OK).body(facilityViewListResp);
    } else if (req.getViewByIndicator() == FilterAutoFinanceAccountListingEnum.ACCOUNT) {
      ApiResponse<AFFSAcctListViewSummaryResp<Object>> accountViewListResp = AFFSService.floorStockingAccountListingByAccount(userAndCorp, req);
      return ResponseEntity.status(HttpStatus.OK).body(accountViewListResp);
    }
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
  }

  @Operation(
          summary = "User Story 2 - 2.13 - Retrieve Floor Stocking Account Detail",
          description = "Retrieve Floor Stocking Account Detail")
  @PostMapping("/acct-dtl")
  @ApiResponses(value = {
          @io.swagger.v3.oas.annotations.responses.ApiResponse(
                  content = @Content(mediaType = "application/json", schema = @Schema(oneOf = {AFFSMasterAcctDetailDTO.class, AFFSCertDetailDTO.class, AFFSSGAcctDetailDTO.class})))
  })
  public ResponseEntity<ApiResponse<?>> getAccountDetail(@RequestHeader(HttpHeaders.AUTHORIZATION) String auth, @Valid @RequestBody AFFSAcctDetailReqDTO request) {
    UserAndCorp userAndCorp = jwtUtil.extractUserIdAndCorpId(auth);
    return ResponseEntity.status(HttpStatus.OK).body(AFFSService.floorStockingAcctDetail(userAndCorp, request));
  }

  @Operation(
          summary = "User Story 2 - 2.13 - Retrieve Floor Stocking Account List",
          description = "Retrieve Floor Stocking Account in the list")
  @PostMapping("/sub-acct-list")
  public ResponseEntity<ApiResponse<AFFSSubAcctListViewSummaryResp>> getSubAccountListing(@RequestHeader(HttpHeaders.AUTHORIZATION) String auth, @Valid @RequestBody AFFSCertListReqDTO request) {
    UserAndCorp userAndCorp = jwtUtil.extractUserIdAndCorpId(auth);
    return ResponseEntity.status(HttpStatus.OK).body(AFFSService.floorStockingSubAcctListing(userAndCorp,request));
  }

  @Operation(
          summary = "User Story 2 - 2.13 - Retrieve Floor Stocking Trx History List",
          description = "Retrieve Floor Stocking Trx History in the list")
  @PostMapping("/trxn-hist")
  @ApiResponses(value = {
          @io.swagger.v3.oas.annotations.responses.ApiResponse(
                  content = @Content(mediaType = "application/json", schema = @Schema(implementation = SearchCollectionResponse.class)))
  })
  public ResponseEntity<ApiResponse<AFFSTrxHistoryResp>> getTrxnHistory(@RequestHeader(HttpHeaders.AUTHORIZATION) String auth, @Valid @RequestBody AFFSTrxHistReqDTO request) {
    return ResponseEntity.status(HttpStatus.OK).body(AFFSService.floorStockingTrxHistory(request, auth));
  }

  @Operation(
          summary = "User Story 2 - 2.13 - Download feature",
          description = "Download feature"
  )
  @PostMapping("/download")
  public ResponseEntity<byte[]> downloadFile(@RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String auth, @RequestBody DownloadReq request) {
    UserAndCorp userAndCorp = jwtUtil.extractUserIdAndCorpId(auth);
    DownloadDTO result = AFFSService.downloadFloorStocking(userAndCorp, request);
    HttpHeaders headers = setHttpHeadersForDownload(result);
    return new ResponseEntity<>(result.getData(), headers, HttpStatus.OK);
  }

}