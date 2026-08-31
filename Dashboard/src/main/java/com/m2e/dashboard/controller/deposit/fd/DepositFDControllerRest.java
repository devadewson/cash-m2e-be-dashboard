package com.m2e.dashboard.controller.deposit.fd;


import com.m2e.dashboard.auth.JwtUtil;
import com.m2e.dashboard.config.UserAndCorp;
import com.m2e.dashboard.dto.base.*;
import com.m2e.dashboard.dto.common.ApiResponse;
import com.m2e.dashboard.dto.deposit.ca.response.DepositAcctListingDTO;
import com.m2e.dashboard.dto.deposit.fd.request.*;
import com.m2e.dashboard.dto.deposit.fd.response.*;
import com.m2e.dashboard.service.deposit.fd.Rest.DepositFDAccountListService;
import com.m2e.dashboard.service.deposit.fd.Rest.DepositFDAccountStatsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@Tag(name = "Deposit FD - Fixed Deposits", description = "Deva Dewson")
@RestController
@Slf4j
@RequestMapping("/deposit/fd/v1")
public class DepositFDControllerRest {

    @Autowired
    JwtUtil jwtUtil;
    @Autowired
    private DepositFDAccountListService depositFDAccountListService;

    @Autowired
    private DepositFDAccountStatsService depositFDAccountStatsService;


    @Operation(
            summary = "US 1, 3, 4 - Get deposit FD summary list",
            description = "Get deposit FD summary list")
    @PostMapping("/acct-list")
    public ResponseEntity<ApiResponse<DepositAcctListingDTO>> getAccountList(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String auth,
            @RequestBody AccountListReq req) {
        UserAndCorp userAndCorp = jwtUtil.extractUserIdAndCorpId(auth);
        return ResponseEntity.ok(depositFDAccountListService.getAccountList(userAndCorp, req)
        );
    }

    @Operation(
            summary = "US 35, 36, 37 - Get fixed deposit stats",
            description = "Get fixed deposit stats"
    )
    @PostMapping("acct-stats")
    public ResponseEntity<ApiResponse<DepositFDAcctStatsDTO>> getDepositFDAcctStats(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String auth,@Valid
            @RequestBody DepositFDAcctStatsReqDTO request) {
        UserAndCorp userAndCorp = jwtUtil.extractUserIdAndCorpId(auth);
        return ResponseEntity.ok(depositFDAccountStatsService.getDepositFDAccountStats(userAndCorp, request));
    }
}