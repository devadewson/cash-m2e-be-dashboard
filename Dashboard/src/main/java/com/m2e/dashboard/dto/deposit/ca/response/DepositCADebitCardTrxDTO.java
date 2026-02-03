package com.m2e.dashboard.dto.deposit.ca.response;

import lombok.Data;

@Data
public class DepositCADebitCardTrxDTO {
    private String date;
    private String trxReference;
    private String trxDesc;
    private String debit;
    private String credit;
    private String currency;
}
