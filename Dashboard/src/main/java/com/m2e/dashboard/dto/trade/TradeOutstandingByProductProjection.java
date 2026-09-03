package com.m2e.dashboard.dto.trade;


import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class TradeOutstandingByProductProjection {

    private final String productType;
    private final String currency;
    private final Long noOfAccounts;
    private final BigDecimal totalOutstandingBal;

    public TradeOutstandingByProductProjection(String productType, String currency,
                                               Long noOfAccounts, BigDecimal totalOutstandingBal) {
        this.productType = productType;
        this.currency = currency;
        this.noOfAccounts = noOfAccounts;
        this.totalOutstandingBal = totalOutstandingBal;
    }
}