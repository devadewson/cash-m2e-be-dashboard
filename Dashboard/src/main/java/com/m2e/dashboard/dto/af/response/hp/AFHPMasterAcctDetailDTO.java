package com.m2e.dashboard.dto.af.response.hp;

import lombok.Data;

@Data
public class AFHPMasterAcctDetailDTO extends AFHPShareAcctDetailDTO {

    private String financedAmt;
    private String outstandingBalance;

    private String facilityLimit;
    private String acctType;
    private String numberOfAcct;
    private String numberOfDaysAllowed;
    private String totalPenalty;
    private String totalUnitExpired;
    private String dateApproved;
}
