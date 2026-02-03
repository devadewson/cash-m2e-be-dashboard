package com.m2e.dashboard.dto.overview.response;

import lombok.Data;

import java.util.List;

@Data
public class AssetLiabilitiesGraphResDTO {

    private List<AssetLiabilitiesGraphSubResDTO> assetGraph;
    private String currency;
    private String lastUpdatedDate;

}
