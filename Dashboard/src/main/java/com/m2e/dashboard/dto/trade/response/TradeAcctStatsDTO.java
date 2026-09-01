package com.m2e.dashboard.dto.trade.response;

import lombok.Data;

import java.io.Serializable;

@Data
public class TradeAcctStatsDTO implements Serializable {
    private String noOfAccounts;
    private String totalOutstandingBal;
    private String lastUpdatedDate;
    private String lastUpdatedDateTime;
    private String baseCurrency;
}
