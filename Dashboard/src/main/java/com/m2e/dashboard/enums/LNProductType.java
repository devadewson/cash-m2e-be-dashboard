package com.m2e.dashboard.enums;

import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Getter
public enum LNProductType {
    CONVENTIONAL("Conventional"),
    ISLAMIC("Islamic");

    private static final Logger logger = LoggerFactory.getLogger(LNProductType.class.getName());
    private final String description;

    LNProductType(String description) {
        this.description = description;
    }

    public static String getLNProductTypeName(String islamicFlag){
        if ("Y".equals(islamicFlag)) {
            return ISLAMIC.getDescription();
        } else if ("N".equals(islamicFlag)) {
            return CONVENTIONAL.getDescription();
        }
        logger.error("IllegalArgument | Invalid islamicFlag: {}", islamicFlag);
        return null;
    }
}
