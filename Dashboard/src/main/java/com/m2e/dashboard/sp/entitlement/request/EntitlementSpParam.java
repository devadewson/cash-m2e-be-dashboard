package com.m2e.dashboard.sp.entitlement.request;

import lombok.Data;

@Data
public class EntitlementSpParam {
    private String userId;
    private String corpId;
    private String corpCifNo;
    private String product;
}
