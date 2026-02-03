package com.m2e.dashboard.dto.overview.request;

import lombok.Data;

@Data
public class AssetLiabilitiesReqDTO {
    private String corpId;
    private String userId;
    private String corpCifNo;
    private String baseCurrency;
}
