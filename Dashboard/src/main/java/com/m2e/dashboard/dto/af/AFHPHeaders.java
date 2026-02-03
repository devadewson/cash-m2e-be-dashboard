package com.m2e.dashboard.dto.af;

import java.util.List;

public class AFHPHeaders {
    public static final String DATE = "Date";
    public static final String TRANSACTION_REFERENCE = "Transaction Reference";
    public static final String TRANSACTION_DESCRIPTION = "Transaction Description";
    public static final String DEBIT = "Debit";
    public static final String CREDIT = "Credit";
    public static final String PROCESSING_TIME = "Processing Time";
    public static final String TRANSACTION_DESCRIPTION_2 = "Transaction Description 2";
    public static final String TRANSACTION_DESCRIPTION_3 = "Transaction Description 3";
    public static final String TRANSACTION_DESCRIPTION_4 = "Transaction Description 4";
    public static final String VIRTUAL_ACCOUNT = "Virtual Account";
    public static final String TRANSACTION_STATUS = "Transaction Status";
    public static final String TRANSACTION_TYPE = "Transaction Type";
    public static final String SOURCE_CODE = "Source Code";
    public static final String TELLER_ID = "Teller ID";
    public static final String OWNERS_INSTITUTION = "Owner's Institution";
    public static final String TRANSACTION_CODE = "Transaction Code";
    public static final String AUXILIARY_TRANSACTION_CODE = "Auxiliary Transaction Code";
    public static final String EXPIRY_DATE = "Expiry Date";
    public static final String CURRENCY = "Currency";

    public static List<String> getAllHeaders() {
        return List.of(
                DATE,
                TRANSACTION_REFERENCE,
                TRANSACTION_DESCRIPTION,
                DEBIT,
                CREDIT,
                PROCESSING_TIME,
                TRANSACTION_DESCRIPTION_2,
                TRANSACTION_DESCRIPTION_3,
                TRANSACTION_DESCRIPTION_4,
                VIRTUAL_ACCOUNT,
                TRANSACTION_STATUS,
                TRANSACTION_TYPE,
                SOURCE_CODE,
                TELLER_ID,
                OWNERS_INSTITUTION,
                TRANSACTION_CODE,
                AUXILIARY_TRANSACTION_CODE,
                EXPIRY_DATE
        );
    }
}

