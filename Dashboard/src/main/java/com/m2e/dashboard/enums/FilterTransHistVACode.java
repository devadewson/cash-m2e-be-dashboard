package com.m2e.dashboard.enums;

import lombok.Getter;

@Getter
public enum FilterTransHistVACode {

    START_DATE("Start Date"),
    END_DATE("End Date"),
    START_TIME("Start Time"),
    END_TIME("End Time"),
    MIN_AMOUNT("Minimum Amount"),
    MAX_AMOUNT("Maximum Amount"),
    TRANSACTION_CODE("Transaction Code");

    final String value;
    FilterTransHistVACode(String value) {
        this.value = value;
    }
}
