package com.m2e.dashboard.enums;

import lombok.Getter;

@Getter
public enum FilterTransHistCustomCode {

    TRANSACTION_TIME("Transaction Time"),
    POSTING_DATE("Posting Date"),
    PROCESSING_TIME("Processing Time"),
    SOURCE_CODE("Source Code"),
    TELLER_ID("Teller ID"),
    TRANSACTION_CODE("Transaction Code");

    final String value;
    FilterTransHistCustomCode(String value) {
        this.value = value;
    }

}
