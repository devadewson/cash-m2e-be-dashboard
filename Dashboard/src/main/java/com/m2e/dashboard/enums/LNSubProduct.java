package com.m2e.dashboard.enums;

import lombok.Getter;

@Getter
public enum LNSubProduct {
    TL("TL"),
    RC("RC"),
    OD("OD");

    private final String value;

    LNSubProduct(String value) {
        this.value = value;
    }
}
