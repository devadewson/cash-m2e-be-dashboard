package com.m2e.dashboard.enums;

import lombok.Getter;

@Getter
public enum InterestPaymentFrequencyMY {
    TERM("1", "Term"),
    MONTHLY("2", "Monthly"),
    HALF_YEARLY("3", "Half Yearly");

    private final String code;
    private final String dscp;

    InterestPaymentFrequencyMY(String code, String dscp) {
        this.code = code;
        this.dscp = dscp;
    }
}
