package com.m2e.dashboard.enums;

import lombok.Getter;

@Getter
public enum InterestPaymentModeMY {
    CASH_CAPITALISED("1", "Add to Principal"),
    TRANSFER_ORDER("2", "Transfer Order"),
    INTEREST_WAIVE("3", "Interest Waive"),
    CREDIT_TO_ACCOUNT("4", "Credit to Account"),
    BY_CHEQUE_CO("5", "By Cheque / CO"),
    INTEREST_WITHHELD("6", "Interest Withheld"),
    CREDIT_TO_MLA("7", "Credit to MLA A/C");

    private final String code;
    private final String dscp;

    InterestPaymentModeMY(String code, String dscp) {
        this.code = code;
        this.dscp = dscp;
    }
}
