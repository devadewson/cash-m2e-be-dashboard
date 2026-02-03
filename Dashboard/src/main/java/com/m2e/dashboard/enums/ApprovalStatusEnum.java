package com.m2e.dashboard.enums;

import lombok.Getter;

@Getter
public enum ApprovalStatusEnum {
    APPROVED("Approved"),
    REJECTED("Rejected"),
    PENDING_APPR("Pending Approval"),
    CANCELLED("Cancelled"),
    NEW("New"),
    DRAFT("Draft"),
    RETURNED("Returned");

    private final String value;

    ApprovalStatusEnum(String value) {
        this.value = value;
    }
}
