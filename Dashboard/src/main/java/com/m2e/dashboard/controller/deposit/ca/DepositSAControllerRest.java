package com.m2e.dashboard.controller.deposit.ca;

import com.m2e.dashboard.auth.JwtUtil;
import com.m2e.dashboard.config.UserAndCorp;
import com.m2e.dashboard.dto.base.AccountListReq;
import com.m2e.dashboard.dto.common.ApiResponse;
import com.m2e.dashboard.dto.deposit.ca.request.DepositAcctStatsReqDTO;
import com.m2e.dashboard.dto.deposit.ca.response.DepositAcctListingDTO;
import com.m2e.dashboard.dto.deposit.ca.response.DepositAcctStatsDTO;
import com.m2e.dashboard.service.deposit.ca.MockDepositDataService;
import com.m2e.dashboard.service.deposit.ca.Rest.DepositCAAccountListService;
import com.m2e.dashboard.service.deposit.ca.Rest.DepositSAAccountListService;
import com.m2e.dashboard.service.deposit.ca.Rest.DepositSAAccountStatsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "M2UBIZM25-180 - Deposit SA - Saving Accounts", description = "PIC: Deva Dewson")
@RestController
@RequestMapping("/deposit/sa/v1")
public class DepositSAControllerRest {

    private static final Logger logger = LoggerFactory.getLogger(com.m2e.dashboard.controller.deposit.ca.DepositCAControllerRest.class);

    @Autowired
    private DepositSAAccountListService depositSAAccountListService;

    @Autowired
    private MockDepositDataService mockDepositDataService;

    @Autowired
    private DepositSAAccountStatsService depositSAAccountStatsService;

    @Autowired
    JwtUtil jwtUtil;

    @Operation(
            summary = "US 1, 3, 4 - Get deposit SA summary list",
            description = "Get deposit SA summary list")
    @GetMapping("/acct-list")
    public ResponseEntity<ApiResponse<DepositAcctListingDTO>> getAccountList(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String auth,
            @RequestBody AccountListReq req) {
            UserAndCorp userAndCorp = jwtUtil.extractUserIdAndCorpId(auth);
            return ResponseEntity.ok(
                    depositSAAccountListService.getAccountList(userAndCorp, req)
            );
        }
    @Operation(
            summary = "US 1, 3, 4 - Get deposit SA stats",
            description = "Get deposit SA stats"
    )
    @PostMapping("acct-stats")
    public ResponseEntity<ApiResponse<DepositAcctStatsDTO>> getAccountStats(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String auth, @Valid
    @RequestBody DepositAcctStatsReqDTO request) {
        UserAndCorp userAndCorp = jwtUtil.extractUserIdAndCorpId(auth);
        return ResponseEntity.ok(depositSAAccountStatsService.getDepositSAAccountStats(userAndCorp, request));
    }

}

