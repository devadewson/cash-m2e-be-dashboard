package com.m2e.dashboard.dto.af.response.bd;

import lombok.Data;

@Data
public class AFBDAcctStatsViewFacilityDTO extends AFBDAcctStatsDTO{
    private String numberOfMasterAcct;
    private String facilityLimit;
}
