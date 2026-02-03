package com.m2e.dashboard.dto.overview.response;

import com.m2e.dashboard.enums.OverviewEnum;

public interface MvEntitlementResDTO {
    String getCorporateId();
    String getCorporateUserId();
    OverviewEnum getProduct();
    String getSubProduct();
}
