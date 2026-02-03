package com.m2e.dashboard.dto.maintenance.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class SubProductDTO implements Serializable {
    private String subProductId;
    private String subProductCode;
    private String subProductDesc;
}
