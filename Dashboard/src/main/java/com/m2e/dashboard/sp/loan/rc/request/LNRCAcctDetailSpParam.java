package com.m2e.dashboard.sp.loan.rc.request;

import lombok.Data;

@Data
public class LNRCAcctDetailSpParam {
    private String corpId;
    private String userId;
    private String acctNumber;
    private String acctCurrency;
}
