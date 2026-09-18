package com.m2e.dashboard.dto.trade;

import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class TradeOutstandingByCifProjection {

    private final String cifNo;
    private final String currency;
    private final BigDecimal totalOutstandingBal;

    public TradeOutstandingByCifProjection(String cifNo, String currency,
                                           BigDecimal totalOutstandingBal) {
        this.cifNo = cifNo;
        this.currency = currency;
        this.totalOutstandingBal = totalOutstandingBal;
    }
}
