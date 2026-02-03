package com.m2e.dashboard.dto.entitlement.response;

import lombok.Data;

import java.util.List;

@Data
public class AcctProdCifEntitlementDto {
    private String corpCifNo;
    private List<AcctProdEntitlementDto> acctProductList;
}
