package com.m2e.dashboard.dto.af.response.pbr;

import lombok.Data;

@Data
public class AFPBRStatisticDTO {
    private String noOfAccounts;
    private String lastUpdatedDateTime;
    private String lastUpdatedDate;
    private String totalFinancedAmount;
    private String totalSettlementAmount;
}