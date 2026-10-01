package com.m2e.dashboard.enums;

import com.m2e.dashboard.exception.EnumNotFoundException;
import lombok.Getter;

@Getter
public enum TradeEnum {
    IMPORT("I", "IMPORT"),
    EXPORT("E", "EXPORT"),
    LOANS("L", "LOANS"),
    INCOMING_COLLECTION("IC", "Incoming Collection"),
    OUTGOING_COLLECTION("OC", "Outgoing Collection"),
    GUARANTEE_ADVISING("GA", "Guarantee Advising"),
    EXPORT_LC_SKBDN_COLLECTION("ELSC", "Export LC/SKBDN Collection"),
    EXPORT_LC_SKBDN_ADVISING("ELSA", "LC/SKBDN Advising"),
    EXPORT_LC_SKBDN_NEGOTIATION("ELSN", "Export LC/SKBDN Negotiation"),
    IMPORT_LC_SKBDN("ILS", "Import LC/SKBDN"),
    GUARANTEE("G", "Guarantee/SBLC"),
    TRADE_FINANCING("TF", "Trade Financing"),
    EXPORT_LC_SKBDN_ACCEPTANCE("ELSACC", "Export LC/SKBDN Acceptance"),
    IMPORT_LC_SKBDN_ACCEPTANCE("ILSACC", "Import LC/SKBDN Acceptance"),

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
