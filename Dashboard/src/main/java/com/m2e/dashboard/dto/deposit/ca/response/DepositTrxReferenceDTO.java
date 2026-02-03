package com.m2e.dashboard.dto.deposit.ca.response;

import lombok.Data;

@Data
public class DepositTrxReferenceDTO {
    private String paymentType;
    private String paymentTypeDesc;
    private String trxAmount;
    private String bankRefNo;
    private String trxType;
    private String acctName;
    private String acctNumber;
    private String payerName;
    private String payerAcct;
    private String payerBankCode;
    private String payerBankName;
    private String trxDate;
    private String totalCharges;
    private String totalChargesCurrency;
    private String vatAmount;
    private String vatAmountCurrency;
    private String creditAmount;
    private String creditAmountCurrency;
    private String paymentDetail;
    private String valueDate;
    private String chequeNo;
    private String uetrNo;
    private String senderName;
    private String bank;
    private String currency;
}
