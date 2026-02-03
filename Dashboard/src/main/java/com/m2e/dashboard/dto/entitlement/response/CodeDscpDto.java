package com.m2e.dashboard.dto.entitlement.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


// use for service package products/funcs return in compAccess endpoint
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CodeDscpDto {
    private String code;
    private String description;
}
