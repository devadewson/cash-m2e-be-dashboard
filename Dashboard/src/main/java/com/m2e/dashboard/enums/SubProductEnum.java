package com.m2e.dashboard.enums;

import lombok.Getter;

@Getter
public enum SubProductEnum {

    // Deposit
    CURRENT_ACCOUNT("CV02", "Deposit Current Account", "CA"),
    VIRTUAL_ACCOUNT("CV02", "Deposit Virtual Account", "VA"),
    FIXED_DEPOSIT("CV05", "Fixed Deposit", "FD"),

    // Autofinance
    BLANKET_HIRE_PURCHASE("CV06", "Auto Finance Business Hire Purchase", "BHP"),
    HIRE_PURCHASE("CV06", "Auto Finance Hire Purchase", "HP"),
    FLOOR_STOCK("CV06", "Auto Finance Floor Stocking", "FS"),
    BLOCK_DISCOUNTING("CV06", "Auto Finance Block Discounting", "BD"),
    PAYMENT_BEFORE_REGISTRATION("CV06", "Auto Finance Payment Before Registration", "PBR"),

    // Card
    CREDIT_CARD("CARDS", "Credit Card", "CRC"),
    CHARGE_CARD("CARDS", "Charge Card", "CHC"),
    DEBIT_CARD("CICSEFT", "Debit Card", "DC"),

    // Investment
    UNIT_TRUST("", "Unit Trusts", "UT"),

    // Trade
    TRADE("", "Trade", "TRD"),
    IMPORT("","Import","I"),
    EXPORT("","Export","E"),
    GUARANTEE("","Guarantee","G"),
    TRADE_LOANS("","Trade Loans","L"),

    // Loan
    TERM_LOAN("CV09", "Loan Term Loan", "TL"),
    TERM_LOAN_FINANCING("CV09", "Loan Term Loan Financing", "TLF"),

    OVERDRAFT("", "Overdraft", "OV"),
    REVOLVING_CREDIT("", "Resolving Credit", "RC"),

    // Other Bank
    OTHER_BANK_DEPOSIT("MT940", "Other Bank - Deposit", "OBD"),
    OTHER_BANK_LOAN("MT941", "Other Bank - Loans/Financing", "OBL"),
    OTHER_BANK_AUTO_FINANCE("MT942", "Other Bank Autofinance", ""),
    OTHER_BANK_INVESTMENT("MT950", "Other Bank Investment", ""),

    // Unknown Sub Product
    SAVINGS("CV02" , "Deposit Savings Account", "SA"),
    MASTER_FOREIGN_CA("CV02", "Deposit Foreign Current Account", "MFCA"),
    VOSTRO_FBK("CV04", "Deposit Vostro Foreign Banker", "FBK"),
    TIME_DEPOSIT("", "Time Deposit", "TD"),
    FOREIGN_CA("", "Foreign CA", "FCA"),
    PAYMENT_BEFORE_TRANSFER("", "Payment Before Transfer", "PBT");


    private final String value;
    private final String description;
    private final String code;
    SubProductEnum(String value, String description, String code) {
        this.value = value;
        this.description = description;
        this.code = code;
    }

}
