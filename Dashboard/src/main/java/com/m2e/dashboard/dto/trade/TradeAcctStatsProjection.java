package com.m2e.dashboard.dto.trade;


import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class TradeAcctStatsProjection {

    private final String currency;
    private final Long noOfAccounts;
    private final BigDecimal totalOutstandingBal;

    public TradeAcctStatsProjection(String currency, Long noOfAccounts,
                                    BigDecimal totalOutstandingBal) {
        this.currency = currency;
        this.noOfAccounts = noOfAccounts;
        this.totalOutstandingBal = totalOutstandingBal;
    }
}
