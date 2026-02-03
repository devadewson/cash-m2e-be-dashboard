package com.m2e.dashboard.enums;

import lombok.Getter;

@Getter
public enum AFAcctType {
    CONVENTIONAL("Conventional"),
    ISLAMIC("Islamic");

    private final String description;

    AFAcctType(String description) {
        this.description = description;
    }
}
