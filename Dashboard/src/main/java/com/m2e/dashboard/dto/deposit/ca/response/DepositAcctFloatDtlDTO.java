package com.m2e.dashboard.dto.deposit.ca.response;

import lombok.Data;

@Data
public class DepositAcctFloatDtlDTO {
    private String trxnCode;
    private String trxnDesc;
    private String floatAmount;
    private String expiryDate;
    private String currency;
}
