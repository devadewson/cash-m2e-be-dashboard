package com.m2e.dashboard.enums;

public enum FileStatusEnum {
    PROCESSING ("The report is still being processed"),
    COMPLETED ("The report has already been completed"),
    FAILED ("The report generation failed");

    private final String description;

    FileStatusEnum(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
