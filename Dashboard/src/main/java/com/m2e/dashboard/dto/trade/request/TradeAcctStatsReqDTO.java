package com.m2e.dashboard.dto.trade.request;

import lombok.Data;

import java.io.Serializable;

@Data
public class TradeAcctStatsReqDTO implements Serializable {
    private String baseCurrency;
}
