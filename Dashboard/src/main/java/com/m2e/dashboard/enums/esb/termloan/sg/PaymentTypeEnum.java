package com.m2e.dashboard.enums.esb.termloan.sg;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Getter
@RequiredArgsConstructor
@Slf4j
public enum PaymentTypeEnum {
    INT_INCLUDED("0", "Interest included"),
    PRIN_ACC_INT("1", "Principal & accrued interest"),
    INTEREST_ONLY("2", "Interest only"),
    PRIN_ONLY("3", "Principal only"),
    SINGLE_PAY("5", "Single pay"),
    PRIN_INT_DUE("6", "Principal & interest due");

    private final String code;
    private final String description;

    public static String getDescriptionByCode(String code) {
        if (code == null) {
            return null;
        }

        for (PaymentTypeEnum paymentType : values()) {
            if (code.equals(paymentType.getCode())) {
                return paymentType.getDescription();
            }
        }
        log.error("Payment type code not found");
        return null;
    }
}