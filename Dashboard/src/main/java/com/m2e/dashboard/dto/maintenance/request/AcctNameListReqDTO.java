package com.m2e.dashboard.dto.maintenance.request;

import com.m2e.dashboard.dto.common.BasePageRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class AcctNameListReqDTO extends BasePageRequest {
    private String searchParam;
    private String corpCifNo;
    @Schema(
            description = "Sub-product code referring to TBL_MT_ACCT_TYP#CD",
            example = "CA",
            allowableValues = {"CA", "FD", "TD", "UT", "TRD", "OV", "RC", "TL", "OD", "HP","FS","BD", "PBR", "DC", "CRC", "CHC"}
    )
    private String subProduct;
}
