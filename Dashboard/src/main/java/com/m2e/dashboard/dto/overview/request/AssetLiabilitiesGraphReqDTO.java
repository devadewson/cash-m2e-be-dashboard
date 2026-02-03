package com.m2e.dashboard.dto.overview.request;

import com.m2e.dashboard.enums.GraphEnum;
import lombok.Data;

@Data
public class AssetLiabilitiesGraphReqDTO {
    private String corpId;
    private String userId;
    private String corpCifNo;
    private String baseCurrency;
    private GraphEnum interval;
}
