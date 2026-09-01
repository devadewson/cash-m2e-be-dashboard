package com.m2e.dashboard.dto.deposit.fd.request;

import lombok.Data;

@Data
public class DepositFDAcctStatsReqDTO {
    private Boolean realTimeIndicator;
    private String baseCurrency;
}
