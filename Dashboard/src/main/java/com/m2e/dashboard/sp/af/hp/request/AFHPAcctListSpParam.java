package com.m2e.dashboard.sp.af.hp.request;

import lombok.Data;

@Data
public class AFHPAcctListSpParam {
    private String userId;
    private String corpId;
    private String corpCifNo;
    private String searchParam;
}
