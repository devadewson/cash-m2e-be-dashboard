package com.m2e.dashboard.dto.investment.response;

import lombok.Data;

@Data
public class INVUnitTrustFundStatsDTO {
    private String dateUpdated;
    private String corpId;
    private Integer totNoOfFunds;
    private String consTotEstMarketValue;
}
