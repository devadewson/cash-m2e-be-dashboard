package com.m2e.dashboard.enums.esb.termloan.my;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Slf4j
public enum RepaymentFreqEnum {
    MONTHLY("1","Monthly"),
    QUARTERLY("2","Quarterly"),
    HALF_YEARLY("3","Half-yearly"),
    YEARLY("4","Yearly"),
    WEEKLY("5","Weekly"),
    MANUAL("6","Manual"),
    FORTNIGHTLY("7","Fortnightly");

    private final String code;
    private final String description;

    RepaymentFreqEnum(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public static String getDescriptionByCode(String code) {
        if (code==null) {
            return null;
        }

        for (RepaymentFreqEnum interestRepayment: values()) {
            if (code.equals(interestRepayment.getCode())) {
                return interestRepayment.getDescription();
            }
        }
        log.error("Repayment code not found");
        return null;
    }
}
