package com.m2e.dashboard.enums;

import lombok.Getter;

@Getter
public enum LNTLFSGSubProductType {
    FITAS("FITAS"),
    SIBS("SIBS");

    private final String description;

    LNTLFSGSubProductType(String description) {
        this.description = description;
    }
}
