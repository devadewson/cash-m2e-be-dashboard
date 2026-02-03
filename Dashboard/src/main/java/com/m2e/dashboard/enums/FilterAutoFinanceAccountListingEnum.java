package com.m2e.dashboard.enums;

import lombok.Getter;

@Getter
public enum FilterAutoFinanceAccountListingEnum {
    ACCOUNT("account"),
    FACILITY("facility");

    private final String filterBy;

    FilterAutoFinanceAccountListingEnum(String filterBy){
        this.filterBy = filterBy;
    }
}
