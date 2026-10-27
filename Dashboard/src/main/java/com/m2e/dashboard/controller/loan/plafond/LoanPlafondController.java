package com.m2e.dashboard.controller.loan.plafond;

import com.m2e.dashboard.auth.JwtUtil;
import com.m2e.dashboard.config.UserAndCorp;
import com.m2e.dashboard.dto.common.ApiResponse;
import com.m2e.dashboard.dto.loan.plafond.request.LoanPlafondReq;
import com.m2e.dashboard.dto.loan.plafond.response.LoanPlafondStatsDTO;
import com.m2e.dashboard.service.loan.plafond.LoanPlafondStatsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Loan - Plafond")
@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/loan/plafond/v1")
public class LoanPlafondController {

    private final LoanPlafondStatsService statsService;
    private final JwtUtil jwtUtil;

    @Operation(summary = "Plafond Stats",
            description = "Plafond CIF dari EDW (IDR), dikonversi ke baseCurrency. "
                    + "lastUpdated = waktu data masuk ke DB.")
    @PostMapping("acct-stats")
    public ResponseEntity<ApiResponse<LoanPlafondStatsDTO>> getStats(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String auth,
            @RequestBody(required = false) LoanPlafondReq req) {

        UserAndCorp userAndCorp = jwtUtil.extractUserIdAndCorpId(auth);
        String baseCurrency = (req != null) ? req.getBaseCurrency() : null;

        log.info("REST request - Plafond Stats | currency={}", baseCurrency);

        return ResponseEntity.ok(statsService.getStats(userAndCorp, baseCurrency));
    }
}
