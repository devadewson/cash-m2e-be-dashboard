package com.m2e.dashboard.sp.trade.response;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

@Data
@SuperBuilder
@NoArgsConstructor(force = true)
public class TradeAcctListingSpDTO implements Serializable {
    private String acctName;
    private String numberOfItems;
    private String currency;
    private String outstandingBalance;
}
