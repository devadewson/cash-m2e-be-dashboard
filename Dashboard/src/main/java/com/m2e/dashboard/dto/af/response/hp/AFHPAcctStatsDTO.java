package com.m2e.dashboard.dto.af.response.hp;

import lombok.Data;

@Data
public class AFHPAcctStatsDTO {
    private String lastUpdatedDateTime;
    private String lastUpdatedDate;
    private String numberOfAcct;
    private String financedAmt;
    private String outstandingBalance;
    private String currency;
}
