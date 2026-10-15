package com.m2e.dashboard.controller.deposit.fd;


import com.m2e.dashboard.service.deposit.fd.Rest.DepositFdSftpService;
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
@RequestMapping("/api/sftp/fd")
@Slf4j
@RequiredArgsConstructor
@Tag(name = "Internal Job - FD Datamart")
public class DepositFdSftpController {

    private final DepositFdSftpService service;

    @Operation(summary = "Trigger FD datamart job",
            description = "Download EDW_LOAN_FD dari SFTP lalu proses ke DATAMART_DEPOSIT_FD.")
    @PostMapping("/trigger")
    public ResponseEntity<?> trigger() {
        log.warn("[FD-SFTP] MANUAL TRIGGER");

        try {
            long elapsed = service.execute();
            return ResponseEntity.ok(Map.of(
                    "status", "COMPLETED",
                    "message", "FD SFTP job selesai",
                    "durationMs", elapsed));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("status", "ABORTED", "message", e.getMessage()));
        } catch (Exception e) {
            log.error("[FD-SFTP] Manual trigger failed", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("status", "FAILED", "message", String.valueOf(e.getMessage())));
        }
    }
}