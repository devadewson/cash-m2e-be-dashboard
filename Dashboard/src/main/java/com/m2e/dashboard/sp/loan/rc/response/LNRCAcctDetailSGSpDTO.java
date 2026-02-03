package com.m2e.dashboard.sp.loan.rc.response;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LNRCAcctDetailSGSpDTO {
    private String acctName;
    private String acctNumber;
    private String accountCurrency;
    private String accountOwnerCompany;
    private String lastUpdated;
    private String repaymentAmount;
    private String outstandingLoanAmount;
    private String interestAmount;
    private String productName;
    private String lateInterestAmount;
    private String originalLoanAmount;
    private String interestRate;
    private String nextInterestRepaymentDate;
    private String nextInterestRefixDate;
    private String provider;

    // FITAS
    private String outstandingFinancingAmount;
    private String profitAmount;
    private String profitRate;
    private String originalFinancingAmount;
    private String latePaymentCharges;
    private String isFitas;
    private String isIslamic;
}