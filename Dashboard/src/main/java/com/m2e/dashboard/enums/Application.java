package com.m2e.dashboard.enums;

import lombok.Getter;

@Getter
public enum Application {
    PORTFOLIO("203"),
    CASHLITE("200"),
    TRADE("201"),
    CUSTODY("101"),
    PORTAL("");

    private final String value;
    Application(String value) { this.value = value; }
}
