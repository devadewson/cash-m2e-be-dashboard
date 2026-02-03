package com.m2e.dashboard.enums;

import lombok.Getter;

@Getter
public enum ESBStatusCodeEnum {
    SUCCESS("0"),
    ERROR("100"),
    HOST_REJECTED("200"),
    PARTIAL_SUCCESS("201"),
    HOST_TIMEOUT("300"),
    ESB_GATEWAY_TIMEOUT("504")
    ;

    private final String value;

    ESBStatusCodeEnum(String value) {
        this.value = value;
    }
}
