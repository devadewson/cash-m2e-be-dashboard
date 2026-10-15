package com.m2e.dashboard.controller.loan.tlf;

import com.m2e.dashboard.auth.JwtUtil;
import com.m2e.dashboard.config.UserAndCorp;
import com.m2e.dashboard.dto.base.loan.LNAcctListReq;
import com.m2e.dashboard.dto.base.loan.LNReq;
import com.m2e.dashboard.dto.common.ApiResponse;
import com.m2e.dashboard.dto.loan.tlf.LoanPbAcctStatsDTO;
import com.m2e.dashboard.dto.loan.tlf.LoanPbFacilityListingDTO;
import com.m2e.dashboard.enums.LoanPbProductEnum;
import com.m2e.dashboard.service.loan.LoanPbListService;
import com.m2e.dashboard.service.loan.LoanPbStatsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Loan - Term Loan/Financing (PB)")
@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/loan/term-loan/v1")
public class LoanPbController {

    private static final LoanPbProductEnum PRODUCT = LoanPbProductEnum.PB;

    private final LoanPbStatsService statsService;
    private final LoanPbListService listService;
    private final JwtUtil jwtUtil;

    @Operation(summary = "PB Account Stats",
            description = "Total original limit dan outstanding dari account list PB, "
                    + "dikonversi ke baseCurrency.")
    @PostMapping("acct-stats")
    public ResponseEntity<ApiResponse<LoanPbAcctStatsDTO>> getAccountStats(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String auth,
            @RequestBody(required = false) LNReq req) {

        UserAndCorp userAndCorp = jwtUtil.extractUserIdAndCorpId(auth);
        String baseCurrency = (req != null) ? req.getBaseCurrency() : null;

        log.info("REST request - PB Account Stats | currency={}", baseCurrency);
        return ResponseEntity.ok(statsService.getStats(userAndCorp, PRODUCT, baseCurrency));
    }

    @Operation(summary = "PB Account List",
            description = "Fasilitas PB berpaginasi, tiap fasilitas berisi seluruh draw-nya.")
    @PostMapping("acct-list")
    public ResponseEntity<ApiResponse<LoanPbFacilityListingDTO>> getAccountList(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String auth,
            @RequestBody LNAcctListReq req) {

        UserAndCorp userAndCorp = jwtUtil.extractUserIdAndCorpId(auth);

        log.info("REST request - PB Account List | page={} pageSize={}", req.getPage(), req.getPageSize());
        return ResponseEntity.ok(listService.getAccountList(userAndCorp, PRODUCT, req));
    }
}