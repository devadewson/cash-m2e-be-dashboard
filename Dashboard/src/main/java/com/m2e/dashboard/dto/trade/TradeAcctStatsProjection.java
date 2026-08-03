package com.m2e.dashboard.dto.trade;

import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class TradeAcctStatsProjection {

    private final Long noOfAccounts;
    private final BigDecimal totalOutstandingBal;

    public TradeAcctStatsProjection(Long noOfAccounts, BigDecimal totalOutstandingBal) {
        this.noOfAccounts = noOfAccounts;
        this.totalOutstandingBal = totalOutstandingBal;
    }
}