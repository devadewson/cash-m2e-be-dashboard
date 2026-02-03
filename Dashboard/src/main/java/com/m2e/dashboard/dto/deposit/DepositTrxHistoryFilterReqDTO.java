package com.m2e.dashboard.dto.deposit;

import lombok.Data;

@Data
public class DepositTrxHistoryFilterReqDTO {
    private String startDate;
    private String startTime;
    private String endDate;
    private String endTime;
    private String amtFrom;
    private String amtTo;
}
