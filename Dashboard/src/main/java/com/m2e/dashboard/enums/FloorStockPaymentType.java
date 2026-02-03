package com.m2e.dashboard.enums;

import lombok.Getter;

@Getter
public enum FloorStockPaymentType {
    P0("(P0)Int included"),
    P1("(P1)Prin+Acc Int"),
    P2("(P2)Interest only"),
    P3("(P3)Prin only"),
    P5("(P5)Single pay"),
    P6("(P6)Prin+Int due");

    private final String paymentType;
    FloorStockPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }
}
