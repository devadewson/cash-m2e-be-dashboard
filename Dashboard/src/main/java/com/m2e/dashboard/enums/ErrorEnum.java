package com.m2e.dashboard.enums;

import lombok.Getter;

@Getter
public enum ErrorEnum {
    ERR_DB_UPDATE("Failed to update database"),
    ERR_MT_HOST_CATEGORY("HOST"),
    ERR_ESB_DEFAULT_CD("UNKNOWN_ERR_CD"),
    ERR_ESB_DEFAULT_DESC("System Not Available");

    private final String value;

    ErrorEnum(String value) {
        this.value = value;
    }
}
