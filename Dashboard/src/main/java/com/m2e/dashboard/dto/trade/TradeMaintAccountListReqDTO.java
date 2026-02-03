package com.m2e.dashboard.dto.trade;

import com.m2e.dashboard.dto.common.BasePageRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class TradeMaintAccountListReqDTO extends BasePageRequest {
    private String searchParam;
    private String corpCifNo;
    @Schema(
            description = "Sub-product code for Trade only",
            example = "I",
            allowableValues = {"I", "E", "L", "G"}
    )
    private String subProduct;
}