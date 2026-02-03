package com.m2e.dashboard.dto.otherbank.response.loan;

import lombok.Data;

@Data
public class OtherBankLoanTrxHistDTO {
    private String date;
    private String trxRefNo;
    private String trxDesc;
    private String debit;
    private String credit;

    //    Customize field
    private String entryDate;
    private String trxType;
    private String trxCode;
    private String outstandingBalance;

    private String trxAmount;
    private String supplementaryDetails;
    private String openingBalance;
    private String debitCurrency;
    private String creditCurrency;
}
