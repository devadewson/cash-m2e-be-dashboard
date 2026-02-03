package com.m2e.dashboard.dto.investment.response;

import lombok.Data;

@Data
public class INVUnitTrustFundSearchDTO {
    private String id;
    private String fundName;
    private String initialCost;
    private String units;
    private String lastClosingNav;
    private String totalValue;
    private String currency;
}
