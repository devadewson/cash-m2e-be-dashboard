package com.m2e.dashboard.sp.af.fs.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AFFSAcctStatsViewByFacilitySpDTO {
    private String numberOfAcct;
    private String consolidatedFacilityLimit;
    private String currency;
    private String lastUpdatedDate;
    private String lastUpdatedDateTime;
}