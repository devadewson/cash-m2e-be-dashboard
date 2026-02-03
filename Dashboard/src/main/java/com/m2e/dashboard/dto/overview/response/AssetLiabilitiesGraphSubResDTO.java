package com.m2e.dashboard.dto.overview.response;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class AssetLiabilitiesGraphSubResDTO {

    private String label;
    private BigDecimal assets;
    private BigDecimal liabilities;
    private BigDecimal netWorth;

}
