package com.m2e.dashboard.enums;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AcctProductEntitlementConst {
    public static final String USER_PRODUCT_LIST_REDIS_KEY = "USER_PRODUCT_LIST";
    public static final String USER_ACCT_PRODUCT_LIST_REDIS_KEY = "USER_ACCT_PRODUCT_LIST";

    public static final String JOMPAY_BILL_PAYMENT_PRODUCT_CODE = "02";
    public static final String PAYMENT_PRODUCT_CODE = "03";
    public static final String LOAN_AND_CARD_PAYMENT_PRODUCT_CODE = "12";
    public static final String MY_FD_PLACEMENT_PRODUCT_CODE = "13";
    public static final String MY_FD_UPLIFTMENT_PRODUCT_CODE = "14";
    public static final String MY_FD_MANAGE_PRODUCT_CODE = "15";
}
