//package com.m2e.dashboard.controller.loan.overdraft;
//
//import com.m2e.dashboard.dto.base.DownloadDTO;
//import com.m2e.dashboard.dto.base.DownloadReq;
//import com.m2e.dashboard.dto.base.loan.LNReq;
//import com.m2e.dashboard.dto.base.loan.LNAcctListReq;
//import com.m2e.dashboard.dto.common.ApiResponse;
//import com.m2e.dashboard.dto.common.SearchCollectionResponse;
//import com.m2e.dashboard.dto.loan.overdraft.request.LNOverdraftDetailReqDTO;
//import com.m2e.dashboard.dto.loan.overdraft.request.LNOverdraftOmnibusDetailReqDTO;
//import com.m2e.dashboard.dto.loan.overdraft.response.LNOverdraftAccountStatisticDTO;
//import com.m2e.dashboard.dto.loan.overdraft.response.LNOverdraftAcctListingDTO;
//import com.m2e.dashboard.dto.loan.overdraft.response.LNOverdraftDetailDTO;
//import com.m2e.dashboard.dto.loan.overdraft.response.LNOverdraftOmnibusItemDTO;
//import com.m2e.dashboard.service.loan.overdraft.LNOverdraftService;
//import io.swagger.v3.oas.annotations.Operation;
//import io.swagger.v3.oas.annotations.media.Content;
//import io.swagger.v3.oas.annotations.media.Schema;
//import io.swagger.v3.oas.annotations.responses.ApiResponses;
//import io.swagger.v3.oas.annotations.tags.Tag;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.MediaType;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import static com.m2e.dashboard.enums.Application.PORTFOLIO;
//import static com.m2e.dashboard.enums.Product.LOAN;
//import static com.m2e.dashboard.enums.SubProductEnum.OVERDRAFT;
//import static com.m2e.dashboard.util.DownloadUtil.setHttpHeadersForDownload;
//
//@Tag(name = "M2UBIZM25-976 Loan - Overdraft", description = "PIC: Merlin")
//@RestController
//@Slf4j
//@RequestMapping("/loan/overdraft/v1")
//public class LNOverdraftController {
//
//  @Autowired private LNOverdraftService service;
//
////  @Operation(summary = "US 1 - Loan Overdraft Account Statistic")
//  @PostMapping("/acct-stats")
//  public ResponseEntity<ApiResponse<LNOverdraftAccountStatisticDTO>> getLoanOverdraftAccountStatistic(@RequestHeader(HttpHeaders.AUTHORIZATION) String auth, @RequestBody LNReq request) {
//    return ResponseEntity.status(HttpStatus.OK).body(service.getLoanOverdraftAccountStatistic(auth, request));
//  }
//
//  @Operation(summary = "US 1 - Loan Overdrafts Account Listing")
//  @PostMapping("/acct-list")
//  public ResponseEntity<ApiResponse<LNOverdraftAcctListingDTO>> getLoanOverdraftAccountListing(@RequestHeader(HttpHeaders.AUTHORIZATION) String auth, @RequestBody LNAcctListReq request) {
//    return ResponseEntity.status(HttpStatus.OK).body(service.getLoanOverdraftAccountListing(auth, request));
//  }
//
//  @Operation(summary = "KIV - Loan Overdrafts Download")
//  @PostMapping(value = "/download", produces = {MediaType.APPLICATION_OCTET_STREAM_VALUE})
//  public ResponseEntity<byte[]> downloadLoanOverdraft(@RequestHeader(HttpHeaders.AUTHORIZATION) String auth, @RequestBody DownloadReq request) {
//    DownloadDTO result = service.downloadLoanOverdraft(auth, request);
//    HttpHeaders headers = setHttpHeadersForDownload(result);
//    return new ResponseEntity<>(result.getData(), headers, HttpStatus.OK);
//  }
//
//  @Operation(summary = "US 2 - Loan Overdrafts Details")
//  @ApiResponses(value = {
//          @io.swagger.v3.oas.annotations.responses.ApiResponse(
//                  content = @Content(mediaType = "application/json",
//                          schema = @Schema(oneOf = {LNOverdraftDetailDTO.class})))
//  })
//  @PostMapping("/acct-dtl")
//  public ResponseEntity<ApiResponse<LNOverdraftDetailDTO>> getLoanOverdraftDetail(@RequestHeader(HttpHeaders.AUTHORIZATION) String auth, @RequestBody LNOverdraftDetailReqDTO request) {
//    return ResponseEntity.status(HttpStatus.OK).body(service.getLoanOverdraftDetail(auth, request));
//  }
//
//  @Operation(summary = "US 2 - Loan Overdrafts Details - Ombinus Accounts")
//  @PostMapping("/acct-dtl-omnibus-accounts")
//  public ResponseEntity<ApiResponse<SearchCollectionResponse<LNOverdraftOmnibusItemDTO>>> getLoanOverdraftDetailOmnibusAccounts(@RequestHeader(HttpHeaders.AUTHORIZATION) String auth, @RequestBody LNOverdraftOmnibusDetailReqDTO request) {
//    return ResponseEntity.status(HttpStatus.OK).body(service.getLoanOverdraftOmnibusAccounts(auth, request));
//  }
//
//}
