package com.m2e.dashboard.dto.otherbank.response.deposit;

import lombok.Data;

import java.io.Serializable;

@Data
public class OtherBankDepositTrxHistoryDTO implements Serializable {
    private String date;
    private String trxRefNo;
    private String trxDesc;
    private String debit;
    private String credit;

    //    Customize field
    private String entryDate;
    private String trxType;
    private String trxCode;
    private String closingBalance;
    private String trxAmount;
    private String supplementaryDetails;
    private String openingBalance;
    private String swiftTrxCode;
    private String debitCurrency;
    private String creditCurrency;
}
