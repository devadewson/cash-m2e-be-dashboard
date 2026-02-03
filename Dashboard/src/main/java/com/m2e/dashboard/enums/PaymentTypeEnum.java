package com.m2e.dashboard.enums;

import lombok.Getter;

@Getter
public enum PaymentTypeEnum {

    GIRO("001", "Outward ACH"),
    SBC("002", "Foreign Bill Collection"),
    RENTAS("003", "RTGS"),
    IWTT("004", "Telegraphic Transfer"),
    TT("005", "Telegraphic Transfer"),
    MEPS("006", "MEPS"),
    DEMAND_DRAFT("007", "Foreign Currency Bank Draft"),
    CASHIER_ORDER("008", "Cashier Order");
    private final String code;
    private final String name;

    PaymentTypeEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public static String mapCodeToValue(String code) {
        for(PaymentTypeEnum paymentTypeEnum :PaymentTypeEnum.values()) {
            if(paymentTypeEnum.code.equals(code))
                return paymentTypeEnum.name;
        }
        throw new IllegalArgumentException("Invalid code : " + code);
    }
}
