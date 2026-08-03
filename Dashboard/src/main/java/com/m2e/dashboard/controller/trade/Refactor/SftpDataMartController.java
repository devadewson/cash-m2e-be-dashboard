package com.m2e.dashboard.controller.trade.Refactor;

import com.m2e.dashboard.service.trade.Refactor.TradeMart.SftpDataMartService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
        service.execute();
        return ResponseEntity.ok(Map.of("status", "OK"));
    }
}