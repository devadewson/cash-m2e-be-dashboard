package com.m2e.dashboard.controller.trade.Refactor;

import com.m2e.dashboard.service.trade.Trademart.SftpDataMartService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/sftp")
@Slf4j
@RequiredArgsConstructor
public class SftpDataMartController {

    private final SftpDataMartService service;

    @PostMapping("/trigger")
    public ResponseEntity<?> trigger() {
        log.warn("[SFTP] MANUAL TRIGGER");

        try {
            long elapsed = service.execute();
            return ResponseEntity.ok(Map.of(
                    "status", "COMPLETED",
                    "message", "SFTP job selesai",
                    "durationMs", elapsed
            ));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("status", "ABORTED", "message", e.getMessage()));
        } catch (Exception e) {
            log.error("[SFTP] Manual trigger failed", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("status", "FAILED", "message", String.valueOf(e.getMessage())));
        }
    }
}