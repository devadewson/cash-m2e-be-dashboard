package com.m2e.dashboard.enums;

import com.m2e.dashboard.service.overview.OverviewService;
import lombok.Getter;

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
    G("G","Guarantee"),
    L("L","Trade Loans"),

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
}
