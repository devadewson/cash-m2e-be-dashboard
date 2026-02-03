package com.m2e.dashboard.dto.overview.request;

import com.m2e.dashboard.enums.OverviewEnum;
import lombok.Data;

@Data
public class OverviewReqDTO {
    private String id;
    private String corpId;
    private String userId;
    private String baseCurrency;
    private String corpCifNo;
    OverviewEnum product;
}
