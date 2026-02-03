package com.m2e.dashboard.dto.af.response.bd;

import lombok.Data;

import java.io.Serializable;

@Data
public class AFBDSubChildAcctListDTO extends AFBDSubAcctListDTO implements Serializable {
    private String agreementNumber;
    private String collateralId;
    private String acctStatusDesc;
    private String installmentAmount;
    private String amountDue;
    private String settlementAmount;
}
