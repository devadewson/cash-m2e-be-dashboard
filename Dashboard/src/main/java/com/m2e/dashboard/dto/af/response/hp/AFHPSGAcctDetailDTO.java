package com.m2e.dashboard.dto.af.response.hp;

import lombok.Data;

@Data
public class AFHPSGAcctDetailDTO extends AFHPShareAcctDetailDTO {

    private String instalmentAmt;
    private String interestRate;
    private String dueDate;
    private String numberOfInstalmentPaid;

    private String originalTenure;
    private String remainingTenure;
    private String collateralIDCarPlate;
    private String collateralInformation;
    private String chassisNumber;
    private String engineNumber;
    private String collateralDescription;
    private String lateCharges;
    private String miscOutOfPocket;
    private String accruedLateCharges;
    private String amountDue;
    private String originalLoanAmt;
    private String outstandingBalance;
    private String settlementAmt;
    private String instalmentDue;
    private String monthlyInstalment;
    private String lateInterest;
    private String tlastIndicator;
}
