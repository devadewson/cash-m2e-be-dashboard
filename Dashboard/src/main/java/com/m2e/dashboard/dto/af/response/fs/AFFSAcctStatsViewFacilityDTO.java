package com.m2e.dashboard.dto.af.response.fs;

import lombok.Data;

@Data
public class AFFSAcctStatsViewFacilityDTO extends AFFSAcctStatsDTO{
    private String numberOfAcct;
    private String consolidatedFacilityLimit;
    private String currency;
}