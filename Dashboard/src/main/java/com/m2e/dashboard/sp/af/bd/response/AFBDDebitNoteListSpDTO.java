package com.m2e.dashboard.sp.af.bd.response;

import lombok.Data;

@Data
public class AFBDDebitNoteListSpDTO {
    private String acctName;
    private String acctNumber;
    private String masterAcctNumber;
    private String provider;
    private String currency;
    private String blockAmt;
    private String disbursementDate;
}
