package com.m2e.dashboard.enums;

public enum OtherBankViewType {
    HISTORICAL_RECORD("historical"),
    INTRADAY("intra");

    private final String viewType;

    OtherBankViewType(String viewType){
        this.viewType = viewType;
    }
}
