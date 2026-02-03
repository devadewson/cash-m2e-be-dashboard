package com.m2e.dashboard.enums;

import lombok.Getter;

@Getter
public enum Product {
    ALL("All","ALL"),
    DEPOSIT("Deposits", "DEPOSIT"),
    TRADE("TRADE", "TRADE"),
    AUTO_FINANCE("Auto Loans/Financing", "AUTOFINANCE"),
    CARD("Cards", "CARDS"),
    INVESTMENT("INVESTMENT", "INVESTMENT"),
    LOAN("Loans/Financing", "LOAN"),
    OTHER_BANK_ACCOUNT("OTHER_BANK_ACCOUNT", "OTHER");

    private final String value;
    private final String code;

    Product(String value, String code) {
        this.value = value;
        this.code = code;
    }
}
