package com.m2e.dashboard.enums;

import lombok.Getter;

@Getter
public enum AccountLevelEnum {
    FACILITY("facility"),
    ACCOUNT("account"),
    SUBACCOUNT("sub account");

    private final String accountLevel;
    AccountLevelEnum(String accountLevel) {
        this.accountLevel = accountLevel;
    }
}
