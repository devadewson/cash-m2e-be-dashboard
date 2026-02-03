package com.m2e.dashboard.enums;

import lombok.Getter;

@Getter
public enum RedirectModuleEnum {
    PAYMENT_ACTION_REDIRECT("paymentActionRedirect");
    private final String beanName;

    RedirectModuleEnum(String beanName) {
        this.beanName = beanName;
    }
}
