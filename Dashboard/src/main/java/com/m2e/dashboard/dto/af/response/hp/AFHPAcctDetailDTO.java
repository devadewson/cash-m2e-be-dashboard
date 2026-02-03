package com.m2e.dashboard.dto.af.response.hp;

import lombok.Data;

@Data
public class AFHPAcctDetailDTO extends AFHPShareAcctDetailDTO {

    private String financedAmt;
    private String outstandingBalance;

    private String interestRate;
    private String dueDate;
    private String numberOfInstalmentPaid;

    private String acctType;
    private String vehicalReg;
    private String financingPeriod;
    private String monthlyInstalment;
    private String amountDue;
    private String numberOfMonthsDue;
    private String instalmentDue;
    private String overdueInterest;
    private String lateCharges;
    private String settlementAmt;
    private String provider;
    private String tlastIndicator;
}
