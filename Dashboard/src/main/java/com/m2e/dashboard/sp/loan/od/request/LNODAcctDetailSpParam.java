package com.m2e.dashboard.sp.loan.od.request;

import lombok.Data;

@Data
public class LNODAcctDetailSpParam {
    private String corpId;
    private String userId;
    private String acctNumber;
    private String acctCurrency;
}
