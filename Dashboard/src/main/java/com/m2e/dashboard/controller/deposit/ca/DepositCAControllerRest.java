package com.m2e.dashboard.controller.deposit.ca;

import com.m2e.dashboard.auth.JwtUtil;
import com.m2e.dashboard.config.UserAndCorp;
import com.m2e.dashboard.dto.base.AccountListReq;
import com.m2e.dashboard.dto.common.ApiResponse;
import com.m2e.dashboard.dto.deposit.ca.request.*;
import com.m2e.dashboard.dto.deposit.ca.response.*;
import com.m2e.dashboard.service.deposit.ca.MockDepositDataService;
import com.m2e.dashboard.service.deposit.ca.Rest.DepositCAAccountDetailService;
import com.m2e.dashboard.service.deposit.ca.Rest.DepositCAAccountListService;
import com.m2e.dashboard.service.deposit.ca.Rest.DepositCAAccountStatsService;
import com.m2e.dashboard.service.trade.TradeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * No US 2
 * User Story with No Endpoints yet
 * US 12 - NOT YET
 * US 5, 14 - Function Entitlement respective to each Account - NOT YET
 * US 6 - Search within every listing endpoint - DONE
 * US 17 - Irrelevant, FE logic only - NA
 * US 18 - Customise field~ hmm - NOT YET
 * US 19 - MT103 - NOT YET
 */
@Tag(name = "Deposit CA - Current Accounts", description = "PIC: Deva Dewson")
@RestController
@Slf4j
@RequestMapping("/deposit/ca/v1")
public class DepositCAControllerRest {

        private static final Logger logger = LoggerFactory.getLogger(DepositCAControllerRest.class);

        @Autowired
        private DepositCAAccountListService depositCAAccountListService;

        @Autowired
        private MockDepositDataService mockDepositDataService;

        @Autowired
        private DepositCAAccountDetailService depositCAAccountDetailService;
        @Autowired
        private DepositCAAccountStatsService depositCAAccountStatsService;
        @Autowired
        private TradeService tradeService;

        @Autowired
        JwtUtil jwtUtil;

        // @Autowired

        // DepositCAService depositCAService;

        @Operation(summary = "US 1, 3, 4 - Get deposit CA summary list", description = "Get deposit CA summary list")
        @PostMapping("/acct-list")
        public ResponseEntity<ApiResponse<DepositAcctListingDTO>> getAccountList(
                        @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String auth,
                        @RequestBody AccountListReq req) {

                UserAndCorp userAndCorp = jwtUtil.extractUserIdAndCorpId(auth);
                return ResponseEntity.ok(
                                depositCAAccountListService.getAccountList(userAndCorp, req));
        }

        @Operation(summary = "US 7, 8, 9, 10, 11 - Retrieve Deposit Account Detail", description = "Retrieve Deposit Account Detail")
        @PostMapping("/acct-dtl")
        public ResponseEntity<ApiResponse<DepositAccountDetailDTO>> getAccountDetail(
                        @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String auth,
                        @Valid @RequestBody DepositAccountDetailReqDTO request) {
                UserAndCorp userAndCorp = jwtUtil.extractUserIdAndCorpId(auth);
                return ResponseEntity.ok(
                                depositCAAccountDetailService.getAccountDetail(userAndCorp, request));

        }

        @Operation(summary = "US 1, 3, 4 - Get deposit CA stats", description = "Get deposit CA stats")
        @PostMapping("acct-stats")
        public ResponseEntity<ApiResponse<DepositAcctStatsDTO>> getAccountStats(
                        @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String auth,
                        @Valid @RequestBody DepositAcctStatsReqDTO request) {
                UserAndCorp userAndCorp = jwtUtil.extractUserIdAndCorpId(auth);
                return ResponseEntity.ok(depositCAAccountStatsService.getDepositCAAccountStats(userAndCorp, request));
        }

}
