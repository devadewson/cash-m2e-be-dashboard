package com.m2e.dashboard.controller.overview;

import com.m2e.dashboard.dto.overview.Refactor.JobRunResultDTO;
import com.m2e.dashboard.service.overview.AssetGraph.AssetSnapshotJobService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/internal/job/asset-snapshot")
@Tag(name = "Internal Job - Asset Snapshot")
public class AssetGraphJobController {

    private final AssetSnapshotJobService jobService;

    @Operation(
            summary = "Trigger job snapshot full run (sinkron)",
            description = "Menunggu sampai semua CIF selesai diproses"
    )
    @PostMapping("/run")
    public ResponseEntity<JobRunResultDTO> run() {

        log.warn("[SNAPSHOT] MANUAL TRIGGER - full run (synchronous)");

        JobRunResultDTO result = jobService.execute();

        HttpStatus status = switch (result.getStatus()) {
            case "COMPLETED", "COMPLETED_WITH_ERRORS" -> HttpStatus.OK;
            case "NO_DATA" -> HttpStatus.OK;
            case "ABORTED" -> HttpStatus.CONFLICT;
            default -> HttpStatus.INTERNAL_SERVER_ERROR;
        };

        return ResponseEntity.status(status).body(result);
    }
}