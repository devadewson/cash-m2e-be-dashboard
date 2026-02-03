package com.m2e.dashboard.enums;

import lombok.Getter;

@Getter
public enum RedisFunctionEnum {
    ACC_PRODUCT_TYPE("Account Product Type"),
    ;

    private final String description;

    RedisFunctionEnum(String description) {
        this.description = description;
    }

}
