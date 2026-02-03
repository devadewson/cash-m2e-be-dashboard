package com.m2e.dashboard.enums;

import lombok.Getter;

@Getter
public enum TaskMoreAction {
    VIEW("View Details"),
    APPROVE("Approve"),
    REJECT("Reject");

    private final String value;
    TaskMoreAction(String value) {
        this.value = value;
    }
}
