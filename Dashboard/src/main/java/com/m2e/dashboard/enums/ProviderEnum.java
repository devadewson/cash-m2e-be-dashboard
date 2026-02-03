package com.m2e.dashboard.enums;

import lombok.Getter;

@Getter
public enum ProviderEnum {
    MY("MYR"),
    SG("SG");

    private final String provider;

    ProviderEnum(String provider){
        this.provider = provider;
    }
}
