package com.m2e.dashboard.sp.entitlement.response;

import lombok.Data;

@Data
public class AcctProductResDto {
    private String productCode;
    private String productDescription;
    private String subProductCode;
    private String subProductDescription;
    private String cifNo;
    private boolean isDowntime;
}
