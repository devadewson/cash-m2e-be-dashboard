package com.m2e.dashboard.dto.loan.rc.request;

import lombok.Data;

import java.io.Serializable;

@Data
public class LNRevolvingCreditDetailReqDTO implements Serializable {
    private String acctNumber;
    private String currency;
    private String provider;
}