package com.m2e.dashboard.enums;


import lombok.Getter;

/** Produk PB/PPB, dibedakan lewat ACCRUE_METHOD di tabel draw. */
@Getter
public enum LoanPbProductEnum {

    PB("04", "Term Loan/Financing"),
    PPB("01", "Revolving Promissory Note Financing");

    private final String accrueMethod;
    private final String description;

    LoanPbProductEnum(String accrueMethod, String description) {
        this.accrueMethod = accrueMethod;
        this.description = description;
    }
}
