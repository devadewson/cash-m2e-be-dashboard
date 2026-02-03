package com.m2e.dashboard.sp.af.pbr.request;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AFPBRAcctStatsSpParam {
    private String userId;
    private String corpId;
    private String viewByIndicator;
}
