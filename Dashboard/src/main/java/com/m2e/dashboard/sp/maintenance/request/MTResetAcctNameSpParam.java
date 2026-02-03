package com.m2e.dashboard.sp.maintenance.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MTResetAcctNameSpParam {
    private String userId;
    private String corpId;
    private String acctNumber;
    private String currency;
    private String acctTypeCode;
}
