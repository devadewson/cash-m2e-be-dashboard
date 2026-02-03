package com.m2e.dashboard.sp.loan.rc.response;

import lombok.Data;

@Data
public class LNRCAcctDetailMYSpDTO {
    private String acctName;
    private String acctNumber;
    private String currency;
    private String accountOwnerCompany;
    private String lastUpdated;
    private String drawdownAmount;
    private String outstandingBalanceAmount;
    private String productName;
    private String productType;
    private String revolvingCreditType;
    private String workItemNumber;
    private String maturityDate;
    private String provider;
}