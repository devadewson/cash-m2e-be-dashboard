package com.m2e.dashboard.controller.trade.Refactor.OutgoingCollection;

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
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Trade - Outgoing Collection", description = "PIC: Deva Dewson")
@RestController
@Slf4j
@RequestMapping("/trade/OutgoingCollection/v1")
public class OutgoingColectionController {

    @Autowired
    TradeService tradeService;

    @Autowired
    JwtUtil jwtUtil;

    @Operation(
            summary = "US 2, 2.1, 2.2 [BOTTOM] - Outgoing Colection Account List",
            description = "Get trades account - Outgoing Colection list")
    @PostMapping("acct-list")
    public ResponseEntity<ApiResponse<TradeAcctListingDTO>> getAccountList
            (@RequestHeader(value = HttpHeaders.AUTHORIZATION,required = false) String auth, @RequestBody AccountListReq req) {
//        String userId = "0011944737";
//        String corpId = "0011944737";
//        UserAndCorp userAndCorp = new UserAndCorp();
//        userAndCorp.setUserId(userId);
//        userAndCorp.setCorpId(corpId);

        UserAndCorp userAndCorp = jwtUtil.extractUserIdAndCorpId(auth);
        return ResponseEntity.ok(tradeService.getTradeAccountListing(userAndCorp, req, TradeEnum.OUTGOING_COLLECTION));
    }

    @Operation(
            summary = "US 2, 2.1, 2.2 [TOP] - Outgoing Collection Account Stats",
            description = "Get trades account - Outgoing Collection stats"
    )
    @PostMapping("acct-stats")
    public ResponseEntity<ApiResponse<TradeAcctStatsDTO>> getAccountStats
            (@RequestHeader(value = HttpHeaders.AUTHORIZATION,required = false) String auth) {
//        String userId = "0011944737";
//        String corpId = "0011944737";
//        UserAndCorp userAndCorp = new UserAndCorp();
//        userAndCorp.setUserId(userId);
//        userAndCorp.setCorpId(corpId);
      UserAndCorp userAndCorp = jwtUtil.extractUserIdAndCorpId(auth);
        return ResponseEntity.ok(tradeService.getTradeAccountStats(userAndCorp, TradeEnum.OUTGOING_COLLECTION));
    }
}
