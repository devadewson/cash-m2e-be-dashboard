package com.m2e.dashboard.enums.esb.termloan.my;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Slf4j
public enum PaymentOptionEnum {
    STD_REPYMT("1","STD_REPYMT"),
    CAP_PYMT("2","CAP_PYMT"),
    SERV_INT("3","SERV_INT"),
    BALLOON_REPYMT("4","BALLOON_REPYMT"),
    CAP_BALLOON_REPYMT("5","CAP_BALLOON_REPYMT"),
    MANUAL("6","MANUAL"),
    YEARLY("7","YEARLY"),
    INTPRIN_PROCOF("8","INTPRIN_PROCOF"),
    INVALID_CODE("9","INVALID_CODE");

    private final String code;
    private final String description;

    PaymentOptionEnum(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public static String getDescriptionByCode(String code) {
        if (code==null) {
            return null;
        }

        for (PaymentOptionEnum paymentOption: values()) {
            if (code.equals(paymentOption.getCode())) {
                return paymentOption.getDescription();
            }
        }
        log.error("Payment option code not found");
        return null;
    }
}