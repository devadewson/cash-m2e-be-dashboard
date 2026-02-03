package com.m2e.dashboard.dto.maintenance.request;

import com.m2e.dashboard.enums.Product;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class SubProductListReqDTO implements Serializable {
    @Schema(description = "Product code referring to TBL_MT_ACCT_PROD_TYP#CD")
    private Product productCode;
}
