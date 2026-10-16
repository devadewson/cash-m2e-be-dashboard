package com.m2e.dashboard.dto.deposit;


import lombok.Getter;
import java.math.BigDecimal;

@Getter
public class DepositFdStatsByCurrencyProjection {

    private final String currency;
    private final Long noOfAccounts;
    private final BigDecimal totalPrincipal;

    public DepositFdStatsByCurrencyProjection(String currency, Long noOfAccounts,
                                              BigDecimal totalPrincipal) {
        this.currency = currency;
        this.noOfAccounts = noOfAccounts;
        this.totalPrincipal = totalPrincipal;
    }
}
