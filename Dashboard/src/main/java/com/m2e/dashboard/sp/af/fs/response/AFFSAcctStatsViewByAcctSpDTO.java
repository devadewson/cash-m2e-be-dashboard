package com.m2e.dashboard.sp.af.fs.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AFFSAcctStatsViewByAcctSpDTO {
    private String numberOfAcct;
    private String consolidatedFinancedAmt;
    private String consolidatedOutstandingBalance;
    private String currency;
    private String lastUpdatedDate;
    private String lastUpdatedDateTime;
}