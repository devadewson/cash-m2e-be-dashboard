package com.m2e.dashboard.controller.trade.Refactor.TradeFinancing;

import com.m2e.dashboard.auth.JwtUtil;
import com.m2e.dashboard.config.UserAndCorp;
import com.m2e.dashboard.dto.base.AccountListReq;
import com.m2e.dashboard.dto.common.ApiResponse;
import com.m2e.dashboard.dto.trade.request.TradeAcctStatsReqDTO;
import com.m2e.dashboard.dto.trade.response.TradeAcctListingDTO;
import com.m2e.dashboard.dto.trade.response.TradeAcctStatsDTO;
import com.m2e.dashboard.enums.TradeEnum;
import com.m2e.dashboard.service.trade.TradeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Trade - Trade Financing", description = "PIC: Deva Dewson")
@RestController
@Slf4j
@RequestMapping("/trade/TradeFinancing/v1")
public class TradeFinancingController {

    @Autowired
    TradeService tradeService;

    @Autowired
    JwtUtil jwtUtil;

    @Operation(
            summary = "US 2, 2.1, 2.2 [TOP] - Trade Financing Account Stats",
            description = "Get trades account - Trade Financing stats"
    )
    @PostMapping("acct-stats")
    public ResponseEntity<ApiResponse<TradeAcctStatsDTO>> getAccountStats
            (@RequestHeader(value = HttpHeaders.AUTHORIZATION,required = false) String auth, @RequestBody TradeAcctStatsReqDTO reqStats) {

        UserAndCorp userAndCorp = jwtUtil.extractUserIdAndCorpId(auth);
        String baseCurrency = (reqStats != null) ? reqStats.getBaseCurrency() : null;
        return ResponseEntity.ok(tradeService.getTradeAccountStats(userAndCorp, TradeEnum.TRADE_FINANCING, baseCurrency));
    }
    @Operation(
            summary = "US 2, 2.1, 2.2 [BOTTOM] - Trade Financing Account List",
            description = "Get trades account - Trade Financing list")
    @PostMapping("acct-list")
    public ResponseEntity<ApiResponse<TradeAcctListingDTO>> getAccountList
            (@RequestHeader(value = HttpHeaders.AUTHORIZATION,required = false) String auth, @RequestBody AccountListReq req) {
        UserAndCorp userAndCorp = jwtUtil.extractUserIdAndCorpId(auth);
        return ResponseEntity.ok(tradeService.getTradeAccountListing(userAndCorp, req, TradeEnum.TRADE_FINANCING));
    }
}






