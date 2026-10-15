package com.m2e.dashboard.dto.loan;


import lombok.Getter;

import java.math.BigDecimal;

/** Hasil agregasi per currency: jumlah baris dan total nominal. */
@Getter
public class LoanPbAmountByCurrencyProjection {

    private final String currency;
    private final Long count;
    private final BigDecimal total;

    public LoanPbAmountByCurrencyProjection(String currency, Long count, BigDecimal total) {
        this.currency = currency;
        this.count = count;
        this.total = total;
    }
}