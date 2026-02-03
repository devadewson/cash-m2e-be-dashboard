package com.m2e.dashboard.dto.maintenance.request;

import com.m2e.dashboard.enums.Product;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Data
public class MTResetAccctNameReqDTO implements Serializable {
    private String acctNumber;
    private String currency;
    @Schema(description = "Product code referring to TBL_MT_ACCT_PROD_TYP#CD")
    private Product product;
    @Schema(
            description = "Sub-product code referring to TBL_MT_ACCT_TYP#CD",
            example = "CA",
            allowableValues = {"CA", "FD", "TD", "UT", "TRD", "OV", "TL", "TLF", "TLS", "RC", "OD", "HP","FS","BD", "PBR", "DC", "CRC", "CHC"}
    )
    private String accountTypeCode;
}