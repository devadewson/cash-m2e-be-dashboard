package com.m2e.dashboard.dto.otherbank.response.common;

import lombok.Data;

@Data
public class OtherBankAcctDetailSpecialDTO {
    private String trxDate;
    private String msgType;
    private String statementReference;
    private String statementNumber;
    private String openingBalance;
    private String closingBalance;
    private String openingCurrency;
    private String closingCurrency;
}
