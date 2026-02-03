package com.m2e.dashboard.controller.af.bd;

import com.m2e.dashboard.auth.JwtUtil;
import com.m2e.dashboard.config.UserAndCorp;
import com.m2e.dashboard.dto.af.request.bd.*;
import com.m2e.dashboard.dto.af.response.bd.AFBDDebitNoteListDTO;
import com.m2e.dashboard.dto.af.response.bd.*;
import com.m2e.dashboard.dto.base.DownloadDTO;
import com.m2e.dashboard.dto.base.DownloadReq;
import com.m2e.dashboard.dto.common.ApiResponse;
import com.m2e.dashboard.dto.common.SearchCollectionResponse;
import com.m2e.dashboard.service.af.bd.AFBlockDiscountingService;
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
import static com.m2e.dashboard.enums.SubProductEnum.BLOCK_DISCOUNTING;
import static com.m2e.dashboard.util.DownloadUtil.setHttpHeadersForDownload;

@Tag(name = "Auto Finance BD - Block Discounting", description = "PIC: Benjamin Wong")
@RestController
@Slf4j
@RequestMapping("/af/bd/v1")
public class AFBlockDiscountingController {

  @Autowired
  JwtUtil jwtUtil;

  @Autowired
  AFBlockDiscountingService afBlockDiscountingService;

  @Operation(
          summary = "User Story 3 - 3.13 - Get Block Discounting stats",
          description = "Get Block Discounting stats"
  )
  @ApiResponses(value = {
          @io.swagger.v3.oas.annotations.responses.ApiResponse(
                  content = @Content(mediaType = "application/json", schema = @Schema(oneOf = {AFBDAcctStatsViewAcctDTO.class, AFBDAcctStatsViewFacilityDTO.class})))
  })
  @PostMapping("acct-stats")
  public ResponseEntity<ApiResponse<AFBDAcctStatsDTO>> getAccountStatistics(@RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String auth, @RequestBody AFBDAcctStatsReqDTO req) {
    UserAndCorp userAndCorp = jwtUtil.extractUserIdAndCorpId(auth);
    return ResponseEntity.status(HttpStatus.OK).body(afBlockDiscountingService.bloorDiscountingAccountStats(req, userAndCorp));
  }

  @Operation(
          summary = "User Story 3 - 3.13 - Get Block Discounting summary list",
          description = "Get Block Discounting summary list")
  @PostMapping("acct-list")
  @ApiResponses(value = {
          @io.swagger.v3.oas.annotations.responses.ApiResponse(
                  content = @Content(mediaType = "application/json", schema = @Schema(oneOf = {AFBDAcctListViewAcctDTO.class, AFBDAcctListViewFacilityDTO.class})))
  })
  public ResponseEntity<ApiResponse<AFBDAcctSummaryDTO>> getAccountListing(@RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String auth, @RequestBody AFBDAccountListReq req) {
    UserAndCorp userAndCorp = jwtUtil.extractUserIdAndCorpId(auth);
    return ResponseEntity.status(HttpStatus.OK).body(afBlockDiscountingService.bloorDiscountingAccountListing(userAndCorp, req));
  }

  @Operation(
          summary = "User Story 3 - 3.13 - Retrieve Block Discounting Account Detail",
          description = "Retrieve Block Discounting Account Detail")
  @PostMapping("/acct-dtl")
  @ApiResponses(value = {
          @io.swagger.v3.oas.annotations.responses.ApiResponse(
                  content = @Content(mediaType = "application/json", schema = @Schema(oneOf = {AFBDMasterAcctDetailDTO.class, AFBDSubAcctDetailDTO.class, AFBDSGAcctDetailDTO.class, AFBDAcctDetailDTO.class})))
  })
  public ResponseEntity<ApiResponse<?>> getAccountDetail(@RequestHeader(HttpHeaders.AUTHORIZATION) String auth, @Valid @RequestBody AFBDAcctDetailReqDTO request) {
    UserAndCorp userAndCorp = jwtUtil.extractUserIdAndCorpId(auth);
    return ResponseEntity.status(HttpStatus.OK).body(afBlockDiscountingService.blockDiscountingAcctDetail(userAndCorp, request));
  }

  @Operation(
          summary = "User Story 3 - 3.13 - Retrieve Block Discounting Account List",
          description = "Retrieve Block Discounting Account in the list")
  @PostMapping("/sub-acct-list")
  @ApiResponses(value = {
          @io.swagger.v3.oas.annotations.responses.ApiResponse(
                  content = @Content(mediaType = "application/json", schema = @Schema(oneOf = {AFBDSubMasterAcctListDTO.class, AFBDSubChildAcctListDTO.class})))
  })
  public ResponseEntity<ApiResponse<AFBDSubAcctSummaryDTO>> getSubAccountListing(@RequestHeader(HttpHeaders.AUTHORIZATION) String auth, @Valid @RequestBody AFBDSubAcctListReqDTO request) {
    UserAndCorp userAndCorp = jwtUtil.extractUserIdAndCorpId(auth);
    return ResponseEntity.status(HttpStatus.OK).body(afBlockDiscountingService.blockDiscountingSubAcctListing(request, userAndCorp));
  }

  @Operation(
          summary = "User Story 3 - 3.13 - Retrieve Block Discounting Trx History List",
          description = "Retrieve Block Discounting Trx History in the list")
  @PostMapping("/trxn-hist")
  public ResponseEntity<ApiResponse<AFBDTrxHistorySummaryDTO>> getTrxnHistory(@RequestHeader(HttpHeaders.AUTHORIZATION) String auth, @Valid @RequestBody AFBDTrxHistoryReqDTO request) {
    UserAndCorp userAndCorp = jwtUtil.extractUserIdAndCorpId(auth);
    return ResponseEntity.status(HttpStatus.OK).body(afBlockDiscountingService.blockDiscountingTrxHistory(request, userAndCorp));
  }

  @Operation(
          summary = "User Story 3 - 3.13 - Download feature",
          description = "Download feature"
  )
  @PostMapping("download")
  public ResponseEntity<byte[]> downloadFile(@RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String auth, @RequestBody DownloadReq request) {
    DownloadDTO result = afBlockDiscountingService.downloadFloorStocking(auth, request);
    HttpHeaders headers = setHttpHeadersForDownload(result);
    return new ResponseEntity<>(result.getData(), headers, HttpStatus.OK);
  }

  @Operation(
          summary = "User Story 3 - 3.13 - Get Block Discounting's Debit Note Detail",
          description = "Get Block Discounting's Debit Note Detail")
  @PostMapping("debit-note-dtl")
  public ResponseEntity<ApiResponse<AFBDDebitNoteDetailDTO>> getDebitNoteDetail(@RequestHeader(HttpHeaders.AUTHORIZATION) String auth, @RequestBody AFBDDebitNoteDetailReqDTO req) {
    UserAndCorp userAndCorp = jwtUtil.extractUserIdAndCorpId(auth);
    return ResponseEntity.status(HttpStatus.OK).body(afBlockDiscountingService.blockDiscountingDebitNoteDetail(userAndCorp, req));
  }

  @Operation(
          summary = "User Story 3 - 3.13 - Get Block Discounting's Debit Note Detail",
          description = "Get Block Discounting's Debit Note Detail")
  @PostMapping("debit-note-list")
  public ResponseEntity<ApiResponse<AFBDDebitNoteSummaryDTO>> getDebitNoteList(@RequestHeader(HttpHeaders.AUTHORIZATION) String auth, @RequestBody AFBDDebitNoteListReqDTO req) {
    UserAndCorp userAndCorp = jwtUtil.extractUserIdAndCorpId(auth);
    return ResponseEntity.status(HttpStatus.OK).body(afBlockDiscountingService.blockDiscountingDebitNoteList(req, userAndCorp));
  }

}
