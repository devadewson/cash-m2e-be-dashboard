package com.m2e.dashboard.dto.overview.Refactor;

import lombok.Data;

@Data
public class KursHistoryResultDTO {

    /** SUCCESS, FAILED, EMPTY */
    private String status = "FAILED";
    private String message;

    private String batchId;
    private String kursDate;
    private String fetchTime;
    private Long durationMs = 0L;

    private Integer totalRows = 0;
    private Integer spotRows = 0;
    private Integer validPairs = 0;
    private String spotValueDate;
}