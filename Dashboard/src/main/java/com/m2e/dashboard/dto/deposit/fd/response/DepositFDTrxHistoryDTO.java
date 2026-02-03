package com.m2e.dashboard.dto.deposit.fd.response;

import lombok.Data;

@Data
public class DepositFDTrxHistoryDTO {
    private String date;
    private String trxReference;
    private String trxDesc;
    private String debit;
    private String credit;
    private String processingTime;
    private String trxDesc2;
    private String expiryDate;
    private String currency;
    private String sourceCode;
    private String tellerId;
    private String branch;
    private String trxCode;
    private String beginningBalance;
    private String endBalance;
}
