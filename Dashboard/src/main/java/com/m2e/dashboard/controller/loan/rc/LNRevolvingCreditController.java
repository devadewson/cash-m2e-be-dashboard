package com.m2e.dashboard.controller.loan.rc;

import com.m2e.dashboard.dto.base.DownloadDTO;
import com.m2e.dashboard.dto.base.DownloadReq;
import com.m2e.dashboard.dto.base.loan.LNReq;
import com.m2e.dashboard.dto.base.loan.LNAcctListReq;
import com.m2e.dashboard.dto.common.ApiResponse;
import com.m2e.dashboard.dto.common.SearchCollectionResponse;
import com.m2e.dashboard.dto.loan.tlf.request.LNTrxHistoryReqDTO;
import com.m2e.dashboard.dto.loan.rc.request.LNRevolvingCreditDetailReqDTO;
import com.m2e.dashboard.dto.loan.rc.response.*;
import com.m2e.dashboard.service.loan.rc.LNRevolvingCreditService;
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

@Tag(name = "M2UBIZM25-976 Loan - Revolving Credit", description = "PIC: Merlin")
@RestController
@Slf4j
@RequestMapping("/loan/rc/v1")
public class LNRevolvingCreditController {
  @Autowired
  private LNRevolvingCreditService service;

  @Operation(summary = "Loan Revolving Credit Account Statistic")
  @PostMapping("/acct-stats")
  public ResponseEntity<ApiResponse<LNRevolvingCreditAccountStatisticDTO>> getLoanRevolvingCreditAccountStatistic(@RequestHeader(HttpHeaders.AUTHORIZATION) String auth, @RequestBody LNReq request) {
    return ResponseEntity.status(HttpStatus.OK).body(service.getLoanRevolvingCreditAccountStatistic(auth, request));
  }

  @Operation(summary = "Loan Revolving Credit Account Listing")
  @PostMapping("/acct-list")
  public ResponseEntity<ApiResponse<LNRevolvingCreditAcctListingDTO>> getLoanRevolvingCreditAccountListing(@RequestHeader(HttpHeaders.AUTHORIZATION) String auth, @RequestBody LNAcctListReq request) {
    return ResponseEntity.status(HttpStatus.OK).body(service.getLoanRevolvingCreditAccountListing(auth, request));
  }

  @Operation(summary = "Loan Revolving Credit Download")
  @PostMapping(value = "/download", produces = {MediaType.APPLICATION_OCTET_STREAM_VALUE})
  public ResponseEntity<byte[]> downloadRevolvingCredit(@RequestHeader(HttpHeaders.AUTHORIZATION) String auth, @RequestBody DownloadReq request) {
    DownloadDTO result = service.downloadLoanRevolvingCredit(auth,request);
    HttpHeaders headers = setHttpHeadersForDownload(result);
    return new ResponseEntity<>(result.getData(), headers, HttpStatus.OK);
  }

  @Operation(summary = "Loan Revolving Credit Details")
  @ApiResponses(value = {
          @io.swagger.v3.oas.annotations.responses.ApiResponse(
                  content = @Content(mediaType = "application/json", schema = @Schema(oneOf = {
                          LNRevolvingCreditDetailMYDTO.class,
                          LNRevolvingCreditDetailSGDTO.class,
                          LNRevolvingCreditDetailConventionalFITASSGDTO.class,
                          LNRevolvingCreditDetailConventionalFITASSGDTO.class}))
          )
  })
  @PostMapping("/acct-dtl")
  public ResponseEntity<ApiResponse<LNRevolvingCreditDetailDTO>> getLoanOverdraftDetails(@RequestHeader(HttpHeaders.AUTHORIZATION) String auth, @RequestBody LNRevolvingCreditDetailReqDTO request) {
    return ResponseEntity.status(HttpStatus.OK).body(service.getLoanRevolvingCreditDetail(auth, request));
  }

  @Operation(summary = "Loan Revolving Credit Transaction History per Account for SG")
  @PostMapping("/trxn-hist")
  public ResponseEntity<ApiResponse<SearchCollectionResponse<LNRevolvingCreditTrxHistoryDTO>>> getTrxHistoriesPerAccountSG(@RequestHeader(HttpHeaders.AUTHORIZATION) String auth, @RequestBody LNTrxHistoryReqDTO request) {
    return ResponseEntity.status(HttpStatus.OK).body(service.getTrxHistorySG(auth, request));
  }
}
