package com.m2e.dashboard.dto.otherbank.request;

import lombok.Data;

@Data
public class OtherBankTrxHistoryReqDTO {
    private String startDate;
    private String startTime;
    private String endDate;
    private String endTime;
    private String amtFrom;
    private String amtTo;
}
