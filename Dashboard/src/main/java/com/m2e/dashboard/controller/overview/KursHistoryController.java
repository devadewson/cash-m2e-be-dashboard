package com.m2e.dashboard.controller.overview;


import com.m2e.dashboard.dto.overview.Refactor.KursHistoryResultDTO;
import com.m2e.dashboard.dto.overview.Refactor.KursResolution;
import com.m2e.dashboard.service.overview.JobKursService.KursHistoryService;
import com.m2e.dashboard.service.overview.JobKursService.KursResolver;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/trapi/job/kurs")
@Tag(name = "Internal Job - Kurs History")
public class KursHistoryController {

    private final KursHistoryService kursHistoryService;
    private final KursResolver kursResolver;

    @Operation(summary = "Ambil getKursAll dari Trapi dan simpan ke DB",
            description = "autosys jam 16:00, sebelum Trapi mati jam 17:00.")
    @PostMapping("/run")
    public ResponseEntity<KursHistoryResultDTO> run() {
        log.warn("[KURS-HIST] TRIGGER - fetch and store");

        KursHistoryResultDTO result = kursHistoryService.fetchAndStore();

        HttpStatus status = "SUCCESS".equals(result.getStatus())
                ? HttpStatus.OK
                : HttpStatus.SERVICE_UNAVAILABLE;

        return ResponseEntity.status(status).body(result);
    }

    @Operation(summary = "Cek kurs aktif saat ini (Trapi atau DB)",
            description = "Untuk verifikasi fallback. Tidak menulis apa pun.")
    @GetMapping("/current")
    public ResponseEntity<?> current() {
        KursResolution res = kursResolver.resolve();

        if (res == null) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(Map.of("message", "Kurs tidak tersedia dari Trapi maupun database"));
        }

        return ResponseEntity.ok(Map.of(
                "source", res.getSource(),
                "valueDate", String.valueOf(res.getValueDate()),
                "fetchTime", res.getFetchTime(),
                "ageHours", res.getAgeHours(),
                "stale", res.isStale(),
                "totalPairs", res.getRateMap().size()
        ));
    }

    @Operation(summary = "Hapus data kurs lama")
    @DeleteMapping("/cleanup")
    public ResponseEntity<Map<String, Object>> cleanup(
            @RequestParam(defaultValue = "90") int retentionDays) {
        int deleted = kursHistoryService.cleanup(retentionDays);
        return ResponseEntity.ok(Map.of("deleted", deleted, "retentionDays", retentionDays));
    }
}