package com.m2e.dashboard.sp.investment.response;

import lombok.Data;

import java.io.Serializable;

@Data
public class INVUnitTrustSearchSpDTO implements Serializable {
    private String acctName;
    private String acctNumber;
    private String currency;
    private Integer noOfFunds;
    private String totalEstMarketValue;
}
