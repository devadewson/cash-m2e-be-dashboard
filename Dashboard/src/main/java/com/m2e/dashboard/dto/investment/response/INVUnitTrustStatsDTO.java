package com.m2e.dashboard.dto.investment.response;

import lombok.Data;

import java.math.BigDecimal;


@Data
public class INVUnitTrustStatsDTO {
    private String corpId;
    private String lastUpdatedDate;
    private String lastUpdatedDateTime;
    private Integer totNoOfAccounts;
    private Integer totNoOfFunds;
    private String consTotEstMarketValue;
}
