package com.m2e.dashboard.enums;

import lombok.Getter;

@Getter
public enum APIClientEnum {
    ERROR("error"),
    STATUS("status");
    private final String description;

    APIClientEnum(String description) {
        this.description = description;
    }
}
