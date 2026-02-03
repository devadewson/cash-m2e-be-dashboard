package com.m2e.dashboard.enums;

import lombok.Getter;

@Getter
public enum CreditCardType {
    VISA("Visa"),
    MASTERCARD("Mastercard "),
    AMEX("AMEX"),
    ALL_CARD("All Card Type");

    private final String description;

    CreditCardType(String description) {
        this.description = description;
    }
}
