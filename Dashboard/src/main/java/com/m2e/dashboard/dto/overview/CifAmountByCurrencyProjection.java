package com.m2e.dashboard.dto.overview;

import lombok.Getter;

import java.math.BigDecimal;

/** Total per CIF per currency, untuk job snapshot yang memproses seluruh CIF sekaligus. */
@Getter
public class CifAmountByCurrencyProjection {

    private final String cifNo;
    private final String currency;
    private final BigDecimal total;

    public CifAmountByCurrencyProjection(String cifNo, String currency, BigDecimal total) {
        this.cifNo = cifNo;
        this.currency = currency;
        this.total = total;
    }
}