package com.m2e.dashboard.enums;

import lombok.Getter;

@Getter
public enum FilterTransHistUtCode {

    START_DATE("Start Date"),
    END_DATE("End Date"),
    FUND_NAME("Fund Name");

    final String value;
    FilterTransHistUtCode(String value) {
        this.value = value;
    }
}
