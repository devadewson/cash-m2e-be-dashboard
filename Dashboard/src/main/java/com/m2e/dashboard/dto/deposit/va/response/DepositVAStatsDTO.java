package com.m2e.dashboard.dto.deposit.va.response;

import lombok.Data;

@Data
public class DepositVAStatsDTO {
    private String lastUpdatedDate;
    private String lastUpdatedDateTime;
    private Integer totNoOfAccounts;
}
