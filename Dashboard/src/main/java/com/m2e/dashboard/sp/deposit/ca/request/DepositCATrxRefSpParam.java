package com.m2e.dashboard.sp.deposit.ca.request;

import lombok.Data;

@Data
public class DepositCATrxRefSpParam {
    String bankRefNo;
    String acctCurrency;
    String userId;
    String corpId;
}
