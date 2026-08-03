package com.m2e.dashboard.controller.trade.Refactor.IncomingColection;

import com.m2e.dashboard.auth.JwtUtil;
import com.m2e.dashboard.config.UserAndCorp;
import com.m2e.dashboard.dto.base.AccountListReq;
import com.m2e.dashboard.dto.common.ApiResponse;
import com.m2e.dashboard.dto.trade.response.TradeAcctListingDTO;
import com.m2e.dashboard.dto.trade.response.TradeAcctStatsDTO;
import com.m2e.dashboard.enums.TradeEnum;
import com.m2e.dashboard.service.trade.TradeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "M2UBIZM25-886 Trade - Incoming Collection")
@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/trade/IncomingCollection/v1")
public class IncomingColectionController {

    private final TradeService tradeService;
    private final JwtUtil jwtUtil;

    @Operation(
            summary = "US 2, 2.1, 2.2 [TOP] - Incoming Collection Account Stats",
            description = "Get trades account - Incoming Collection stats"
    )
    @PostMapping("/acct-stats")
    public ResponseEntity<ApiResponse<TradeAcctStatsDTO>> getAccountStats(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION,required = false) String auth) {
        String userId = "LCUS-NYTCOMCO";
        String corpId = "LCUS-NYTCOMCO";
        UserAndCorp userAndCorp = new UserAndCorp();
        userAndCorp.setUserId(userId);
        userAndCorp.setCorpId(corpId);
//        UserAndCorp userAndCorp = jwtUtil.extractUserIdAndCorpId(auth);
        log.info("[IncomingColection] getAccountStats called");
        return ResponseEntity.ok(tradeService.getTradeAccountStats(userAndCorp,
                TradeEnum.INCOMING_COLLECTION));
    }

    @Operation(
            summary = "US 2, 2.1, 2.2 [BOTTOM] - Incoming Colection Account List",
            description = "Get trades account - Incoming Colection list")
    @PostMapping("acct-list")
    public ResponseEntity<ApiResponse<TradeAcctListingDTO>> getAccountList
            (@RequestHeader(value = HttpHeaders.AUTHORIZATION,required = false) String auth, @RequestBody AccountListReq req) {
        String userId = "LCUS-NYTCOMCO";
        String corpId = "LCUS-NYTCOMCO";
        UserAndCorp userAndCorp = new UserAndCorp();
        userAndCorp.setUserId(userId);
        userAndCorp.setCorpId(corpId);

        //UserAndCorp userAndCorp = jwtUtil.extractUserIdAndCorpId(auth);
        return ResponseEntity.ok(tradeService.getTradeAccountListing(userAndCorp, req, TradeEnum.INCOMING_COLLECTION));
    }
}