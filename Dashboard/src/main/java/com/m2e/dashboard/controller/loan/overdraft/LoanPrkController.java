package com.m2e.dashboard.controller.loan.overdraft;


import com.m2e.dashboard.auth.JwtUtil;
import com.m2e.dashboard.config.UserAndCorp;
import com.m2e.dashboard.dto.base.loan.LNAcctListReq;
import com.m2e.dashboard.dto.base.loan.LNReq;
import com.m2e.dashboard.dto.common.ApiResponse;
import com.m2e.dashboard.dto.loan.overdraft.response.LNOverdraftAccountStatisticDTO;
import com.m2e.dashboard.dto.loan.overdraft.response.LNOverdraftAcctListingDTO;
import com.m2e.dashboard.service.loan.overdraft.LoanPrkListService;
import com.m2e.dashboard.service.loan.overdraft.LoanPrkStatsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Loan - PRK / Overdraft")
@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/loan/overdraft/v1")
public class LoanPrkController {

    private final LoanPrkStatsService statsService;
    private final LoanPrkListService listService;
    private final JwtUtil jwtUtil;

    @Operation(summary = "PRK Account Stats",
            description = "Total limit dan utilised amount, dikonversi ke baseCurrency "
                    + "karena menjumlahkan lintas rekening.")
    @PostMapping("acct-stats")
    public ResponseEntity<ApiResponse<LNOverdraftAccountStatisticDTO>> getAccountStats(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String auth,
            @RequestBody(required = false) LNReq req) {

        UserAndCorp userAndCorp = jwtUtil.extractUserIdAndCorpId(auth);
        String baseCurrency = (req != null) ? req.getBaseCurrency() : null;

        log.info("REST request - PRK Account Stats | currency={}", baseCurrency);

        return ResponseEntity.ok(statsService.getStats(userAndCorp, baseCurrency));
    }

    @Operation(summary = "PRK Account List",
            description = "Daftar rekening PRK dengan currency asli dari host, tanpa konversi.")
    @PostMapping("acct-list")
    public ResponseEntity<ApiResponse<LNOverdraftAcctListingDTO>> getAccountList(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String auth,
            @RequestBody LNAcctListReq req) {

        UserAndCorp userAndCorp = jwtUtil.extractUserIdAndCorpId(auth);

        log.info("REST request - PRK Account List | page={} pageSize={}",
                req.getPage(), req.getPageSize());

        return ResponseEntity.ok(listService.getAccountList(userAndCorp, req));
    }
}
