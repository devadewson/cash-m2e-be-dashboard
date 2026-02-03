package com.m2e.dashboard.dto.deposit.ca.response;

import lombok.Data;

/**
 *
 */
@Data
public class DepositTrxHistoryDTO {
    private String date;
    private String processingTime;
    private String trxReference;
    private String trxDesc;
    private String trxDesc2;
    private String trxDesc3;
    private String trxDesc4;
    private String trxDesc5;
    private String trxDesc6;
    private String currency;
    private String debit;
    private String credit;
    private String sourceCode;
    private String tellerId;
    private String branch;
    private String trxCode;
    private String auxTrxCode;
    private String expiryDate;
    private String beginBalance;
    private String endBalance;
    private String virtualAcct;
}
