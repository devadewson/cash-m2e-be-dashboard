package com.m2e.dashboard.controller.loan.overdraft;

import com.m2e.dashboard.service.loan.jobDataMart.LoanPrkSftpService;
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
@RequestMapping("/api/sftp/prk")
@Slf4j
@RequiredArgsConstructor
@Tag(name = "Internal Job - PRK Datamart")
public class LoanPrkSftpController {

    private final LoanPrkSftpService service;

    @Operation(summary = "Trigger PRK datamart job",
            description = "Download EDW_LOAN_PRK.TXT dari SFTP lalu proses ke DATAMART_LOAN_PRK.")
    @PostMapping("/trigger")
    public ResponseEntity<?> trigger() {
        log.warn("[PRK-SFTP] MANUAL TRIGGER");

        try {
            long elapsed = service.execute();
            return ResponseEntity.ok(Map.of(
                    "status", "COMPLETED",
                    "message", "PRK SFTP job selesai",
                    "durationMs", elapsed));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("status", "ABORTED", "message", e.getMessage()));
        } catch (Exception e) {
            log.error("[PRK-SFTP] Manual trigger failed", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("status", "FAILED", "message", String.valueOf(e.getMessage())));
        }
    }
}