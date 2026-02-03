package com.m2e.dashboard.dto.deposit.ca.response;

import lombok.Data;

@Data
public class DepositCAEODBalanceDTO {
    private String createdDate;
    private String closingLedgerBalance;
    private String closingAvailableBalance;
    private String currency;
}
