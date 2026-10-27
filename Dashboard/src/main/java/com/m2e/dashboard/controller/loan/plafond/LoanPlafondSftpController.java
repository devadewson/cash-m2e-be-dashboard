package com.m2e.dashboard.controller.loan.plafond;


import com.m2e.dashboard.service.loan.plafond.LoanPlafondSftpService;
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
@RequestMapping("/api/sftp/plafond")
@Slf4j
@RequiredArgsConstructor
@Tag(name = "Internal Job - Plafond Datamart")
public class LoanPlafondSftpController {

    private final LoanPlafondSftpService service;

    @Operation(summary = "Trigger plafond datamart job",
            description = "Download EDW_Plafond.TXT dari SFTP lalu proses ke DATAMART_PLAFOND.")
    @PostMapping("/trigger")
    public ResponseEntity<?> trigger() {
        log.warn("[PLAFOND-SFTP] MANUAL TRIGGER");
        try {
            long elapsed = service.execute();
            return ResponseEntity.ok(Map.of(
                    "status", "COMPLETED",
                    "message", "Plafond SFTP job selesai",
                    "durationMs", elapsed));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("status", "ABORTED", "message", e.getMessage()));
        } catch (Exception e) {
            log.error("[PLAFOND-SFTP] Manual trigger failed", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("status", "FAILED", "message", String.valueOf(e.getMessage())));
        }
    }
}
