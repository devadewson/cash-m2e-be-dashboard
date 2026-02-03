package com.m2e.dashboard.enums;

import lombok.Getter;

@Getter
public enum InterestPaymentModeSG {
    BY_CHEQUE_CO("C", "By Cheque / CO"),
    CREDIT_TO_ACCOUNT("D", "Credit to Account"),
    CASH_CAPITALIZED("T", "Cash / Capitalized");

    private final String code;
    private final String dscp;

    InterestPaymentModeSG(String code, String dscp) {
        this.code = code;
        this.dscp = dscp;
    }
}
