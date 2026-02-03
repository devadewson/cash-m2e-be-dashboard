package com.m2e.dashboard.enums;

import lombok.Getter;

@Getter
public enum LNOverdraftType {

    OMNIBUS("Omnibus"),
    INTRADAY("Intraday"),
    NORMAL("Normal");

    private final String description;

    LNOverdraftType(String description) {
        this.description = description;
    }
}
