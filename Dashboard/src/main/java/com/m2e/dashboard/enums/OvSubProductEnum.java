package com.m2e.dashboard.enums;

import com.m2e.dashboard.service.overview.OverviewService;
import lombok.Getter;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
public enum OvSubProductEnum implements OverviewService.Order {
    // Deposit
    FCA("FCA","Foreign Current Account"),
    CA("CA","Current Account"),
    FD("FD","Fixed Deposit"),
    TD("TD","Term Deposit"),
    SA("SA","Saving Account"),
    FBK("FBK","Vostro/FBK"),
    MFCA("MFCA","Master Foreign Current Account"),

    //virtual Acc
    VA("VA","Virtual Account"),

    //Loan
    TL("TL","Term Loan/Term Financing"),
    OD("OD","Overdraft"),
    RC("RC","Revolving Credit"),

    //investment
    UT("UT","Unit Trust"),

    //Trades category
    TRD("TRD","Trades"),
    I("I","Import"),
    E("E","Export"),
    INCOMING_COLLECTION("IC", "Incoming Collection"),
    OUTGOING_COLLECTION("OC", "Outgoing Collection"),
    GUARANTEE_ADVISING("GA", "Guarantee Advising"),
    EXPORT_LC_SKBDN_COLLECTION("ELSC", "Export LC/SKBDN Collection"),
    EXPORT_LC_SKBDN_ADVISING("ELSA", "LC/SKBDN Advising"),
    EXPORT_LC_SKBDN_NEGOTIATION("ELSN", "Export LC/SKBDN Negotiation"),
    IMPORT_LC_SKBDN("ILS", "Import LC/SKBDN"),
    GUARANTEE("G", "Guarantee/SBLC"),
    TRADE_FINANCING("TF", "Trade Financing"),

    //Cards
    CRC("CRC","Credit Card"),
    CHC("CHC","Charge Card"),
    DC("DC","Debit Card"),

    HP("HP", "Hire Purchase") {
        @Override
        public int getPosition() {
            return 1;
        }
    },
    BHP("BHP", "Blanket/Business Hire Purchase") {
        @Override
        public int getPosition() {
            return HP.getPosition() + 1;
        }
    },
    PBR("PBR", "Payment Before Registration") {
        @Override
        public int getPosition() {
            return BD.getPosition() + 1;
        }
    },
    BD("BD", "Block Discounting") {
        @Override
        public int getPosition() {
            return FS.getPosition() + 1;
        }
    },
    FS("FS", "Floor Stock") {
        @Override
        public int getPosition() {
            return BHP.getPosition() + 1;
        }
    },

    //other bank
    DEPOSIT("DEPOSIT","Other Bank Deposit"),
    LOAN("LOAN","Other Bank Loan");

    private final String value;
    private final String description;
    OvSubProductEnum(String value, String description) {
        this.value = value;
        this.description = description;
    }
    // ==================== TRADE BUCKET ====================

    public enum TradeBucket { IMPORT, EXPORT, GUARANTEE, FINANCING, NONE }

    private static final Set<OvSubProductEnum> BUCKET_IMPORT =
            EnumSet.of(IMPORT_LC_SKBDN, INCOMING_COLLECTION);

    private static final Set<OvSubProductEnum> BUCKET_EXPORT =
            EnumSet.of(OUTGOING_COLLECTION, EXPORT_LC_SKBDN_COLLECTION,
                    EXPORT_LC_SKBDN_ADVISING, EXPORT_LC_SKBDN_NEGOTIATION);

    private static final Set<OvSubProductEnum> BUCKET_GUARANTEE =
            EnumSet.of(GUARANTEE, GUARANTEE_ADVISING);

    private static final Map<String, OvSubProductEnum> BY_DESCRIPTION =
            Arrays.stream(values())
                    .collect(Collectors.toMap(
                            e -> e.description.trim().toUpperCase(),
                            e -> e,
                            (a, b) -> a));

    /** Cari enum dari description, untuk mencocokkan PRODUCT_TYPE di DATAMART. */
    public static OvSubProductEnum fromDescription(String description) {
        return (description == null) ? null
                : BY_DESCRIPTION.get(description.trim().toUpperCase());
    }

    public TradeBucket getTradeBucket() {
        if (BUCKET_IMPORT.contains(this))    return TradeBucket.IMPORT;
        if (BUCKET_EXPORT.contains(this))    return TradeBucket.EXPORT;
        if (BUCKET_GUARANTEE.contains(this)) return TradeBucket.GUARANTEE;
        if (this == TRADE_FINANCING)         return TradeBucket.FINANCING;
        return TradeBucket.NONE;
    }
}
