package com.m2e.dashboard.sp.af.bd.request;

import lombok.Data;

@Data
public class AFBDAgreementDetailSpParam {
    private String acctNumber;
    private String blockNumber;
    private String agreementNumber;
    private String corpId;
    private String userId;
}
