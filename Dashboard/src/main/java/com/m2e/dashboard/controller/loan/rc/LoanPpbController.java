package com.m2e.dashboard.controller.loan.rc;

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

@Tag(name = "Loan - Revolving Promissory Note Financing (PPB)")
@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/loan/revolving-pn/v1")
public class LoanPpbController {

    private static final LoanPbProductEnum PRODUCT = LoanPbProductEnum.PPB;

    private final LoanPbStatsService statsService;
    private final LoanPbListService listService;
    private final JwtUtil jwtUtil;

    @Operation(summary = "PPB Account Stats",
            description = "Total original limit dan outstanding dari account list PPB, "
                    + "dikonversi ke baseCurrency.")
    @PostMapping("acct-stats")
    public ResponseEntity<ApiResponse<LoanPbAcctStatsDTO>> getAccountStats(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String auth,
            @RequestBody(required = false) LNReq req) {

        UserAndCorp userAndCorp = jwtUtil.extractUserIdAndCorpId(auth);
        String baseCurrency = (req != null) ? req.getBaseCurrency() : null;

        log.info("REST request - PPB Account Stats | currency={}", baseCurrency);
        return ResponseEntity.ok(statsService.getStats(userAndCorp, PRODUCT, baseCurrency));
    }

    @Operation(summary = "PPB Account List",
            description = "Fasilitas PPB berpaginasi, tiap fasilitas berisi seluruh draw-nya.")
    @PostMapping("acct-list")
    public ResponseEntity<ApiResponse<LoanPbFacilityListingDTO>> getAccountList(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String auth,
            @RequestBody LNAcctListReq req) {

        UserAndCorp userAndCorp = jwtUtil.extractUserIdAndCorpId(auth);

        log.info("REST request - PPB Account List | page={} pageSize={}", req.getPage(), req.getPageSize());
        return ResponseEntity.ok(listService.getAccountList(userAndCorp, PRODUCT, req));
    }
}