package com.m2e.dashboard.dto.deposit.ca.response;

import lombok.Data;

@Data
public class DepositOriginatingBankDTO {
    private String remitterBank;
    private String acceptanceDateTime;
    private String elapsedTime;
    private String amountRemitted;
    private String charges;
    private String exchangeRate;
    private String currency;
}
