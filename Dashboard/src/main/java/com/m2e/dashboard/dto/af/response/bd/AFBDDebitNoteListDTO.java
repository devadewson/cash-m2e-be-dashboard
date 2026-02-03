package com.m2e.dashboard.dto.af.response.bd;

import lombok.Data;

@Data
public class AFBDDebitNoteListDTO {
    private String acctName;
    private String acctNumber;
    private String currency;
    private String blockAmt;
    private String disbursementDate;
}
