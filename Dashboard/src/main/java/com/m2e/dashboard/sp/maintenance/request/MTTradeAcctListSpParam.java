package com.m2e.dashboard.sp.maintenance.request;

import com.m2e.dashboard.dto.common.BasePageRequest;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MTTradeAcctListSpParam extends BasePageRequest {
    private String userId;
    private String corpId;
    private String corpCifNo;
    private String searchParam;
    private String subProduct;
}
