package com.m2e.dashboard.enums;

import com.m2e.dashboard.exception.EnumNotFoundException;
import lombok.Getter;

@Getter
public enum TradeEnum {
    IMPORT("I", "IMPORT"),
    EXPORT("E", "EXPORT"),
    LOANS("L", "LOANS"),
    GUARANTEE("G", "GUARANTEE"),
    ALL("", "");
    private final String category;
    private final String acctType;
    TradeEnum(String category, String acctType) {
        this.category = category;
        this.acctType = acctType;
    }

    public static TradeEnum fromString(String category) {
        if (category == null) {
            throw new IllegalArgumentException("Enum class and value must not be null");
        }

        for (TradeEnum tradeEnum : TradeEnum.values()) {
            if (tradeEnum.category.equalsIgnoreCase(category)) {
                return tradeEnum;
            }
        }

        throw new EnumNotFoundException("Enum value not found for class " + TradeEnum.class.getSimpleName() + ": " + category);
    }
}
