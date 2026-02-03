package com.m2e.dashboard.dto.af.response.fs;

import lombok.Data;

@Data
public class AFFSAcctStatViewAcctDTO extends AFFSAcctStatsDTO {
    private String numberOfAcct;
    private String consolidatedFinancedAmt;
    private String consolidatedOutstandingBalance;
    private String currency;
}