package com.m2e.dashboard.enums;

import lombok.Getter;

@Getter
public enum FilterVACode {

    VIRTUAL_ACC_NO("Virtual Account Number"),
    CORPORATE_CODE("Corporate Code");

    final String value;
    FilterVACode(String value) {
        this.value = value;
    }

}
