//package com.m2e.dashboard.controller.deposit.ca;
//
//import com.m2e.dashboard.config.UserAndCorp;
//import com.m2e.dashboard.dto.base.*;
//import com.m2e.dashboard.dto.common.ApiResponse;
//import com.m2e.dashboard.dto.common.SearchCollectionResponse;
//import com.m2e.dashboard.dto.deposit.va.request.*;
//import com.m2e.dashboard.dto.deposit.va.response.*;
//import com.m2e.dashboard.service.deposit.va.DepositVAService;
//import com.m2e.dashboard.auth.JwtUtil;
//import io.swagger.v3.oas.annotations.Operation;
//import io.swagger.v3.oas.annotations.media.Content;
//import io.swagger.v3.oas.annotations.media.Schema;
//import io.swagger.v3.oas.annotations.tags.Tag;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.MediaType;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//import static com.m2e.dashboard.util.DownloadUtil.setHttpHeadersForDownload;
//
//@Tag(name = "M2UBIZM25-180 - Deposit VA - Virtual Accounts", description = "PIC: Adibah")
//@RestController
//@Slf4j
//@RequestMapping("/deposit/va/v1")
//public class DepositVAController {
//    private final DepositVAService virtualAccService;
//    private final JwtUtil jwtUtil;
//    @Autowired
//    public DepositVAController(DepositVAService virtualAccService, JwtUtil jwtUtil) {
//        this.virtualAccService = virtualAccService;
//        this.jwtUtil = jwtUtil;
//    }
//
//    @Operation(
//        summary = "US 22 - Virtual Account Parent Entry",
//        description = "Get Page of Virtual Account based on Current Account"
//    )
//    @PostMapping("/list-per-ca")
//    public ResponseEntity<ApiResponse<SearchCollectionResponse<DepositVASearchDto>>> getListVirtualAcc(@RequestHeader(HttpHeaders.AUTHORIZATION) String auth, @RequestBody DepositVASearchReqDTO virtualAccSearchReqDto) {
//        UserAndCorp userAndCorp = jwtUtil.extractUserIdAndCorpId(auth);
//        return ResponseEntity.status(HttpStatus.OK).body(virtualAccService.getListVaPerCa(userAndCorp,virtualAccSearchReqDto));
//    }
//
//    @Operation(
//        summary = "US 23,26 - Virtual Account Download",
//        description = "Download Virtual Account List & Download Trx Hist of VA"
//    )
//    @PostMapping(value = "/download", produces = {MediaType.APPLICATION_OCTET_STREAM_VALUE})
//    @io.swagger.v3.oas.annotations.parameters.RequestBody(
//            content = @Content(schema = @Schema(oneOf = {DepositVADownloadReqDTO.class, DepositVATransHistDownloadReqDTO.class}))
//    )
//    public ResponseEntity<byte[]> downloadVirtualAccount(@RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String auth, @RequestBody DownloadReq request) {
//        UserAndCorp userAndCorp = jwtUtil.extractUserIdAndCorpId(auth);
//        DownloadDTO result = virtualAccService.downloadVAFiles(userAndCorp, request);
//        HttpHeaders headers = setHttpHeadersForDownload(result);
//        return new ResponseEntity<>(result.getData(), headers, HttpStatus.OK);
//    }
//
//    @Operation(
//        summary = "US 24 - Virtual Account Details",
//        description = "Get Virtual Account Details"
//    )
//    @PostMapping("/acct-dtl")
//    public ResponseEntity<com.m2e.dashboard.dto.common.ApiResponse<DepositVADTO>> getVirtualAccDetails(@RequestHeader(HttpHeaders.AUTHORIZATION) String auth, @RequestBody DepositVAReqDTO virtualAccReqDto) {
//        return ResponseEntity.status(HttpStatus.OK).body(virtualAccService.getVirtualAccountDetails(virtualAccReqDto));
//    }
//
//    @Operation(
//        summary = "US 25 - Virtual Account Transaction History List",
//        description = "Virtual Account Transaction History List"
//    )
//    @PostMapping("/trxn-hist")
//    public ResponseEntity<ApiResponse<DepositVATrxHistoryDTO>> getListVaTransHistory(@RequestHeader(HttpHeaders.AUTHORIZATION) String auth, @RequestBody DepositVATransHistSearchReqDTO transHistVaSearchReqDto) {
//        UserAndCorp userAndCorp = jwtUtil.extractUserIdAndCorpId(auth) ;
//        return ResponseEntity.status(HttpStatus.OK).body(virtualAccService.getTransHistListVirtualAccount(userAndCorp, transHistVaSearchReqDto));
//    }
//
//    @Operation(
//            summary = "US 28 - Virtual Account for Linking Subscriber",
//            description = "Get List of Virtual Account based on Linking Subscriber"
//    )
//    @PostMapping("/acct-list")
//    public ResponseEntity<ApiResponse<DepositVASummaryResp>> getAccountList(@RequestHeader(HttpHeaders.AUTHORIZATION) String auth, @RequestBody DepositVAAccSearchReqDTO req) {
//        UserAndCorp userAndCorp = jwtUtil.extractUserIdAndCorpId(auth);
//        return ResponseEntity.status(HttpStatus.OK).body(virtualAccService.getListVirtualAccount(userAndCorp, req));
//    }
//
//    @Operation(
//            summary = "Virtual Account stats",
//            description = "Main Header for Virtual Account, Total Number of All Required"
//    )
//    @PostMapping("/acct-stats")
//    public ResponseEntity<com.m2e.dashboard.dto.common.ApiResponse<DepositVAStatsDTO>> getAccountStats(@RequestHeader(HttpHeaders.AUTHORIZATION) String auth, @RequestBody DepositVAStatsReqDTO depositVAStatsReqDTO) {
//        return ResponseEntity.status(HttpStatus.OK).body(virtualAccService.getMainVirtualAccountDetails(jwtUtil.extractUserIdAndCorpId(auth),depositVAStatsReqDTO));
//    }
//
//    @Operation(
//                summary = "US 23 & 28 - Virtual Account Dropdown Search",
//            description = "Get Virtual Account Dropdown"
//    )
//    @PostMapping("/dropdown-va")
//    public ResponseEntity<ApiResponse<List<DepositVADropdownDTO>>> getDropdownVACorpCode(@RequestHeader(HttpHeaders.AUTHORIZATION) String auth, @RequestBody DepositVADropdownReqDTO depositVADropdownReqDTO) {
//        UserAndCorp userAndCorp = jwtUtil.extractUserIdAndCorpId(auth);
//        return ResponseEntity.status(HttpStatus.OK).body(virtualAccService.getDropdownVACorpCode(userAndCorp, depositVADropdownReqDTO));
//    }
//
//    @Operation(
//            summary = "Backlog VA - CA in Virtual Account Dropdown Search in Trx Hist",
//            description = "Get Current Account Dropdown for Trx Hist"
//    )
//    @PostMapping("/dropdown-ca-trx-hist")
//    public ResponseEntity<ApiResponse<List<DepositCADropdownDTO>>> getDropdownCa(@RequestHeader(HttpHeaders.AUTHORIZATION) String auth) {
//        return ResponseEntity.status(HttpStatus.OK).body(virtualAccService.getDropdownCa(jwtUtil.extractUserIdAndCorpId(auth)));
//    }
//
//    @Operation(
//            summary = "Backlog Virtual Account Dropdown Search in Trx Hist",
//            description = "Get Current Account Dropdown for Trx Hist"
//    )
//    @PostMapping("/dropdown-va-trx-hist")
//    public ResponseEntity<ApiResponse<List<DepositVATrxHistDropdownDTO>>> getDropdownVa(@RequestHeader(HttpHeaders.AUTHORIZATION) String auth, @RequestBody DepositVADropdownReqDTO depositVADropdownReqDTO) {
//        return ResponseEntity.status(HttpStatus.OK).body(virtualAccService.getDropdownVa(jwtUtil.extractUserIdAndCorpId(auth),depositVADropdownReqDTO));
//    }
//}
