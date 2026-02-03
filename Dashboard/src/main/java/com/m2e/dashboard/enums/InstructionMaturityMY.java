package com.m2e.dashboard.enums;

import lombok.Getter;

@Getter
public enum InstructionMaturityMY {
    NO_RENEWAL_FULL_DISB("1", "Non-Renewal"),
    RENEWAL_ALLOWED("2", "Auto Renewal"),
    FUTURE_RENEWAL("3", "Future Renewal"),
    HOLD_FUTURE_INSTRC("4", "Hold Future Inscription"),
    RO_ANOTHER_PROD("5", "Credit to Account"),
    COMBINE_ROLL("6", "Combine Roll");

    private final String code;
    private final String dscp;

    InstructionMaturityMY(String code, String dscp) {
        this.code = code;
        this.dscp = dscp;
    }
}
