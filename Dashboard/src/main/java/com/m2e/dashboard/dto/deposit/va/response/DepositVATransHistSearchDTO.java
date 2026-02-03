package com.m2e.dashboard.dto.deposit.va.response;

import lombok.Data;


@Data
public class DepositVATransHistSearchDTO {

    private String date;
    private String virtualAcctNumber;
    private String virtualAcctName;
    private String virtualAcctRefNumber;
    private String trxDescription;

    private String debit;
    private String credit;

    private String processingTime;
    private String trxCode;
    private String postingDate;
    private String sourceCode;
    private String trxTime;
    private String tellerId;
    private String currency;

    private String transactionDate;

}
