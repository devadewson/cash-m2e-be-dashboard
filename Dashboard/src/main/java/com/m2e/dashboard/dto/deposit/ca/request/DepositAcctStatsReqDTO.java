package com.m2e.dashboard.dto.deposit.ca.request;

import lombok.Data;

@Data
public class DepositAcctStatsReqDTO {
    private Boolean realTimeIndicator;
    private String baseCurrency;
}
