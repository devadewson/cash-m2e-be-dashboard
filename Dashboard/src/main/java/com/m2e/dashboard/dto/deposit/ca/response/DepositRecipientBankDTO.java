package com.m2e.dashboard.dto.deposit.ca.response;

import lombok.Data;

@Data
public class DepositRecipientBankDTO {
    private String recipientBank;
    private String acceptanceDateTime;
    private String elapsedTime;
    private String amountCredited;
    private String charges;
    private String exchangeRate;
    private String currency;
}
