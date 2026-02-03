package com.m2e.dashboard.enums;

import lombok.Getter;

@Getter
public enum DowntimeCategory {
    GLOBAL("GLOBAL"),
    PROVIDER("PROVIDER"),
    APPLICATION("APPLICATION"),
    PRODUCT("PRODUCT");

    final String value;
    DowntimeCategory(String value) {
        this.value = value;
    }
}
