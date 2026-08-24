package com.m2e.dashboard.dto.overview.Refactor;


import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class JobRunResultDTO {

    /** COMPLETED, COMPLETED_WITH_ERRORS, NO_DATA, ABORTED, FAILED */
    private String status;
    private String message;

    private String jobRunId;
    private String snapshotDate;
    private String startTime;
    private String endTime;
    private Long durationMs;

    private Integer totalCif;
    private Integer insertedCount;
    private Integer successCount;
    private Integer partialCount;
    private Integer emptyCount;
    private Integer failedCount;

    private Integer rateMapSize;
    private String kursValueDate;

    private List<FailedCifDTO> failedCifs = new ArrayList<>();

    @Data
    public static class FailedCifDTO {
        private String corpCifNo;
        private String errorType;
        private String errorMessage;
    }
}
