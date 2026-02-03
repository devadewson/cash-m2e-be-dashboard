package com.m2e.dashboard.enums;

import lombok.Getter;

@Getter
public enum InstructionMaturity {
    A("Auto renewal"),
    N("No auto renewal"),
    C("Payment by cheque"),
    D("Credit to Account"),
    T("Pay by Cash8");

    final String value;
    InstructionMaturity(String value) {
        this.value = value;
    }
}
