package com.m2e.dashboard.enums;

import lombok.Getter;

@Getter
public enum TermLoanTypeEnum {
    MY("MY"),
    SG_SIBS("SG SIBS"),
    SG_FITAS("SG FITAS");

    private final String termLoanType;
    TermLoanTypeEnum(String termLoanType) { this.termLoanType = termLoanType; }
}
