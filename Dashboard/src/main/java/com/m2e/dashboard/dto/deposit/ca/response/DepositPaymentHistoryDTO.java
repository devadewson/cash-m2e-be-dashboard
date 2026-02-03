package com.m2e.dashboard.dto.deposit.ca.response;

import lombok.Data;

import java.util.List;

@Data
public class DepositPaymentHistoryDTO {
    private String date;
    private String bankRefNo;
    private String paymentType;
    private String paymentTypeDesc;
    private String trxType;
    private String trxAmount;
    private String debit;
    private String credit;
    private String uetrNumber;
    private String currency;
    private String creditDebitCurrency;
    private Boolean isViewMt103; //field for FE to check whether to display mt103 or not
    private List<String> entitleAction;
}
