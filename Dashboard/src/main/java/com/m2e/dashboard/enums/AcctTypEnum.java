package com.m2e.dashboard.enums;

public enum AcctTypEnum {
    FCA("Foreign Current Account"),
    CA("Current Account"),
    TD("Fixed Deposit/Term Deposit"),
    SA("Saving Account"),
    FBK("Vostro/FBK"),
    MFCA("Master Foreign Current Account"),
    TL("Term Loan/Term Financing"),
    OD("Overdraft"),
    RC("Revolving Credit"),
    UT("Unit Trust"),
    I("Trade - Import"),
    E("Trade - Export"),
    L("Trade - Loans"),
    IC("Incoming Collection"),
    OC("Outgoing Collection"),
    GA("Guarantee Advising"),
    LSA("LC/SKBDN Advising"),
    ELSC("LC/SKBDN Advising"),
    ELSN("Export LC/SKBDN Negotiation"),
    ILS("Import LC/SKBDN "),
    G("Guarantee/SBLC"),
    TF("Trade Financing"),

    CRC("Credit Card"),
    CHC("Charge Card"),
    DC("Debit Card"),
    MT940("MT940"),
    MT941("MT941"),
    MT942("MT942"),
    MT950("MT950"),
    VA("Virtual Account"),
    HP("Hire Purchase"),
    BHP("Blanket/Business Hire Purchase"),
    PBR("Payment Before Registration"),
    BD("Block Discounting"),
    FS("Floor Stock")
    ;

    private final String description;
    AcctTypEnum(String description) {
        this.description = description;
    }
}
