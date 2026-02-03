package com.m2e.dashboard.sp.loan.tlf.request;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LNTLFAcctDetailSpParam {
    private String acctNumber;
    private String acctCurrency;
    private String userId;
    private String corpId;
}