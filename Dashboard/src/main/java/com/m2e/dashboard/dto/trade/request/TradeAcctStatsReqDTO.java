package com.m2e.dashboard.dto.trade.request;

import lombok.Data;

import java.io.Serializable;

@Data
public class TradeAcctStatsReqDTO implements Serializable {
    private String corpId;
    private String userId;
    private String provider;
    private String tradeCategory;
    private String acctType;
    private Boolean isTodayToggle;
}
