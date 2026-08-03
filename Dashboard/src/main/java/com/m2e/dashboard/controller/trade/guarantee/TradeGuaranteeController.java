//package com.m2e.dashboard.controller.trade.guarantee;
//
//import com.m2e.dashboard.auth.JwtUtil;
//import com.m2e.dashboard.config.UserAndCorp;
//import com.m2e.dashboard.dto.base.*;
//import com.m2e.dashboard.dto.common.ApiResponse;
//import com.m2e.dashboard.dto.trade.request.download.TradeAccountListingDownloadReqDTO;
//import com.m2e.dashboard.dto.trade.response.TradeAcctListingDTO;
//import com.m2e.dashboard.dto.trade.response.TradeAcctStatsDTO;
//import com.m2e.dashboard.enums.TradeEnum;
//import com.m2e.dashboard.service.trade.TradeService;
//import io.swagger.v3.oas.annotations.Operation;
//import io.swagger.v3.oas.annotations.tags.Tag;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.MediaType;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import static com.m2e.dashboard.util.DownloadUtil.setHttpHeadersForDownload;
//
//@Tag(name = "M2UBIZM25-886 Trade - Guarantee", description = "PIC: Wei Xian")
//@RestController
//@Slf4j
//@RequestMapping("/trade/guarantee/v1")
//public class TradeGuaranteeController {
//
//    @Autowired
//    TradeService tradeService;
//    @Autowired
//    JwtUtil jwtUtil;
//
//    @Operation(
//            summary = "US 4, 4.1, 4.2 [BOTTOM] - Trade Guarantee Account List",
//            description = "Get trades account - guarantee list")
//    @PostMapping("acct-list")
//    public ResponseEntity<ApiResponse<TradeAcctListingDTO>> getAccountList(@RequestHeader(HttpHeaders.AUTHORIZATION) String auth, @RequestBody AccountListReq req) {
//        UserAndCorp userAndCorp = jwtUtil.extractUserIdAndCorpId(auth);
//        return ResponseEntity.ok(tradeService.getTradeAccountListingSp(userAndCorp, req, TradeEnum.GUARANTEE));
//    }
//
//    @Operation(
//            summary = "US 4, 4.1, 4.2 [TOP] - Trade Export Account Stats",
//            description = "Get trades account - guarantee stats"
//    )
//    @PostMapping("acct-stats")
//    public ResponseEntity<ApiResponse<TradeAcctStatsDTO>> getAccountStats(@RequestHeader(HttpHeaders.AUTHORIZATION) String auth) {
//        UserAndCorp userAndCorp = jwtUtil.extractUserIdAndCorpId(auth);
//        return ResponseEntity.ok(tradeService.getTradeAccountStatsSp(userAndCorp, TradeEnum.GUARANTEE));
//    }
//
//    @Operation(summary = "Trades account Download")
//    @PostMapping(value = "/download", produces = {MediaType.APPLICATION_OCTET_STREAM_VALUE})
//    public ResponseEntity<byte[]> downloadReport(@RequestHeader(HttpHeaders.AUTHORIZATION) String auth, @RequestBody TradeAccountListingDownloadReqDTO request) {
//        UserAndCorp userAndCorp = jwtUtil.extractUserIdAndCorpId(auth);
//        DownloadDTO result = tradeService.downloadTrade(userAndCorp, request, TradeEnum.GUARANTEE);
//        HttpHeaders headers = setHttpHeadersForDownload(result);
//        return new ResponseEntity<>(result.getData(), headers, HttpStatus.OK);
//    }
//}
//
