package com.m2e.dashboard.dto.entitlement.response;

import lombok.Data;

import java.util.List;

@Data
public class AcctProdEntitlementDto {
    private String code;
    private String description;
    private List<CodeDscpDto> subProducts;
}
