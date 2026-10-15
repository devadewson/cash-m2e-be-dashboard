package com.m2e.dashboard.dto.loan.overdraft;

import lombok.Getter;

import java.math.BigDecimal;

/**
 * Agregasi PRK per currency.
 * totalUtilised = jumlah CURRBAL negatif dijadikan positif (plafond terpakai).
 */
@Getter
public class LoanPrkStatsByCurrencyProjection {

    private final String currency;
    private final Long noOfAccounts;
    private final BigDecimal totalLimit;
    private final BigDecimal totalUtilised;
    private final BigDecimal totalInterestDue;

    public LoanPrkStatsByCurrencyProjection(String currency, Long noOfAccounts,
                                            BigDecimal totalLimit,
                                            BigDecimal totalUtilised,
                                            BigDecimal totalInterestDue) {
        this.currency = currency;
        this.noOfAccounts = noOfAccounts;
        this.totalLimit = totalLimit;
        this.totalUtilised = totalUtilised;
        this.totalInterestDue = totalInterestDue;
    }
}