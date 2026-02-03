package com.m2e.dashboard.sp.otherbank.response;

import lombok.Data;

import java.io.Serializable;

@Data
public class OtherBankLoanAcctListSpDTO implements Serializable {
    private String trxDate;
    private String acctName;
    private String acctNumber;
    private String currency;
    private String senderBIC;
    private String msgType;
    private String closingBalance;
}