package com.m2e.dashboard.dto.af.response.bd;

import lombok.Data;

import java.io.Serializable;

@Data
public class AFBDAcctListViewFacilityDTO extends AFBDAcctListDTO implements Serializable {

    private String masterAcctName;
    private String numberOfAcct;
    private String totalFacilityLimit;
    private String totalOutstandingBalance;
}
