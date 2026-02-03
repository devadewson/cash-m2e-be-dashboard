package com.m2e.dashboard.dto.af.request;

import lombok.Data;

@Data
public class AFTrxHistoryFilterDTO {
    private String startDate;
    private String startTime;
    private String endDate;
    private String endTime;
    private String amtFrom;
    private String amtTo;
    private String trxCode;
}
