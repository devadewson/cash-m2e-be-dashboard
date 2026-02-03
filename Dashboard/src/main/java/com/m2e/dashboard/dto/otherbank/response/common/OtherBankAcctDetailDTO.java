package com.m2e.dashboard.dto.otherbank.response.common;

import lombok.Data;

@Data
public class OtherBankAcctDetailDTO {
    private String acctName;
    private String acctNumber;
    private String currency;
    private String lastUpdatedDateTime;

    private String openingBalance;
    private String closingBalance;

    private String bankName;
    private String acctType;
    private String senderBIC;

    private String msgType;
}
