package com.m2e.dashboard.sp.otherbank.response;

import lombok.Data;

import java.io.Serializable;

@Data
public class OtherBankDepositAcctListingSpDTO implements Serializable {
    private String dateTime;
    private String acctName;
    private String acctNumber;
    private String currency;
    private String senderBIC;
    private String messageType;
    private String closingBalance;
}