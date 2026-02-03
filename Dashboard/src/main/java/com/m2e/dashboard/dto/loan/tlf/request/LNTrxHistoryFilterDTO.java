package com.m2e.dashboard.dto.loan.tlf.request;

import lombok.Data;

@Data
public class LNTrxHistoryFilterDTO {
    private String startDate;
    private String startTime;
    private String endDate;
    private String endTime;
    private String amtFrom;
    private String amtTo;
    private String trxCode;
}
