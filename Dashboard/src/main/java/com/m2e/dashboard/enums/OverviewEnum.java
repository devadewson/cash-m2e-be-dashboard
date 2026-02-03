package com.m2e.dashboard.enums;

import lombok.Getter;

@Getter
public enum OverviewEnum {
    // Deposit
    DEPOSIT("DEPOSIT","Deposit","assets"),
    // Auto-Finance
    AUTOFINANCE("AUTO LOAN/FINANCING","Auto Loans/Financing","liabilities"),
    // Investment
    INVESTMENT("INVESTMENT","Investment","assets"),
    // Loan
    LOAN("LOAN","Loans/Financing","liabilities"),
    //Cards
    CARDS("CARDS","Cards","liabilities"),
    // Trade
    TRADE("TRADE","Trade","liabilities"),
    // Other Banks
    OTHER("OTHER BANKS","Other Banks","assets"),
    //Virtual Account
    VIRTUALACCOUNT("VIRTUAL ACCOUNT","Virtual Account","assets"),
    ;

    private final String value;
    private final String description;
    private final String tab;
    OverviewEnum(String value,String description, String tab) {
        this.value = value;
        this.description = description;
        this.tab = tab;
    }
}
