package com.m2e.dashboard.dto.af.response.bd;

import lombok.Data;

@Data
public class AFBDAcctStatsViewAcctDTO extends AFBDAcctStatsDTO{
    private String numberOfAcct;
    private String financedAmt;
}
