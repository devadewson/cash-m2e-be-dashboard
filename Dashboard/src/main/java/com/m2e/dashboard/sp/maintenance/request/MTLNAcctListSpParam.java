package com.m2e.dashboard.sp.maintenance.request;

import com.m2e.dashboard.dto.common.BasePageRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
public class MTLNAcctListSpParam extends BasePageRequest {
    private String userId;
    private String corpId;
    private String corpCifNo;
    private String searchParam;
    private String subProduct;
}
