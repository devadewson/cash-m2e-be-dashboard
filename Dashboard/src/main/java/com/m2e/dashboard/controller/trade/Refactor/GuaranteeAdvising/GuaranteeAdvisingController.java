package com.m2e.dashboard.controller.trade.Refactor.GuaranteeAdvising;


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

@Tag(name = "Trade - Guarantee Advising", description = "PIC: Deva Dewson")
@RestController
@Slf4j
@RequestMapping("/trade/GuaranteeAdvising/v1")
public class GuaranteeAdvisingController {

    @Autowired
    TradeService tradeService;

    @Autowired
    JwtUtil jwtUtil;

    @Operation(
            summary = "US 2, 2.1, 2.2 [TOP] - Guarantee Advising Account Stats",
            description = "Get trades account - Guarantee Advising stats"
    )
    @PostMapping("acct-stats")
    public ResponseEntity<ApiResponse<TradeAcctStatsDTO>> getAccountStats
            (@RequestHeader(value = HttpHeaders.AUTHORIZATION,required = false) String auth, @RequestBody TradeAcctStatsReqDTO reqStats) {

        UserAndCorp userAndCorp = jwtUtil.extractUserIdAndCorpId(auth);
        String baseCurrency = (reqStats != null) ? reqStats.getBaseCurrency() : null;
        return ResponseEntity.ok(tradeService.getTradeAccountStats(userAndCorp, TradeEnum.GUARANTEE_ADVISING,baseCurrency));
    }

    @Operation(
            summary = "US 2, 2.1, 2.2 [BOTTOM] - Guarantee Advising Account List",
            description = "Get trades account - Guarantee Advising list")
    @PostMapping("acct-list")
    public ResponseEntity<ApiResponse<TradeAcctListingDTO>> getAccountList
            (@RequestHeader(value = HttpHeaders.AUTHORIZATION,required = false) String auth, @RequestBody AccountListReq req) {
//        String userId = "LCUS-00000098";
//        String corpId = "LCUS-00000098";
//        UserAndCorp userAndCorp = new UserAndCorp();
//        userAndCorp.setUserId(userId);
//        userAndCorp.setCorpId(corpId);

        UserAndCorp userAndCorp = jwtUtil.extractUserIdAndCorpId(auth);
        return ResponseEntity.ok(tradeService.getTradeAccountListing(userAndCorp, req, TradeEnum.GUARANTEE_ADVISING));
    }
}

