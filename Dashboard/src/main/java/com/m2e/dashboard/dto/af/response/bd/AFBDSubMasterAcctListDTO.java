package com.m2e.dashboard.dto.af.response.bd;

import lombok.Data;

import java.io.Serializable;

@Data
public class AFBDSubMasterAcctListDTO extends AFBDSubAcctListDTO implements Serializable {
    private String acctName;
    private String acctNumber;
    private String debitNote;
    private String amountDue;

    private String acctStatusDesc;
}
