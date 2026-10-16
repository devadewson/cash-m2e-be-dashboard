package com.m2e.dashboard.controller.loan;

import com.m2e.dashboard.service.loan.jobDataMart.LoanPbDataLoader;
import com.m2e.dashboard.service.loan.jobDataMart.LoanPbSftpService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/sftp/loan-pb")
@Slf4j
@RequiredArgsConstructor
@Tag(name = "Internal Job - Loan PB/PPB Datamart")
public class LoanPbSftpController {

    private final LoanPbSftpService service;

    @Operation(summary = "Trigger PB/PPB datamart job",
            description = "Unduh file fasilitas dan draw, lalu muat ke datamart dalam satu transaksi.")
    @PostMapping("/trigger")
    public ResponseEntity<?> trigger() {
        log.warn("[PB-SFTP] MANUAL TRIGGER");
        long start = System.currentTimeMillis();

        try {
            LoanPbDataLoader.LoadResult result = service.execute();
            return ResponseEntity.ok(Map.of(
                    "status", "COMPLETED",
                    "facilityRows", result.facilityRows(),
                    "drawRows", result.drawRows(),
                    "durationMs", System.currentTimeMillis() - start));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("status", "ABORTED", "message", e.getMessage()));
        } catch (Exception e) {
            log.error("[PB-SFTP] Manual trigger failed", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("status", "FAILED", "message", String.valueOf(e.getMessage())));
        }
    }
}
