package com.m2e.dashboard.dto.investment.response;

import lombok.Data;


@Data
public class INVUnitTrustDTO {

    //details
    private String corpId;
    private String estMarketValue;
    private Double lastClosingNav;

    private String currency;
    private String acctNumber;
    private String fundName;
    private String dateFundOpened;
    private String reinvestDividend;
    private String pendingSubscription;
    private String pendingRedemptionUnit;
    private String unitsPledged;
    private String units;
    private String initialCost;

    private String dateCreated;
    private String dateUpdated;
}
