package com.m2e.dashboard.dto.maintenance.request;

import com.m2e.dashboard.dto.common.BasePageRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class MTOBAccountListReqDTO extends BasePageRequest {
    private String searchParam;
    @Schema(
            description = "Sub-product code for Other Bank only",
            example = "DEPOSIT",
            allowableValues = {"DEPOSIT", "LOAN"}
    )
    private String subProduct;
}