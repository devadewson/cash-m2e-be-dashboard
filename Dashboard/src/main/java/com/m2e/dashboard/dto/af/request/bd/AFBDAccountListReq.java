package com.m2e.dashboard.dto.af.request.bd;

import com.m2e.dashboard.dto.common.BasePageRequest;
import com.m2e.dashboard.enums.FilterAutoFinanceAccountListingEnum;
import com.m2e.dashboard.enums.ProviderEnum;
import lombok.Data;

@Data
public class AFBDAccountListReq extends BasePageRequest {
    private boolean realTimeIndicator;
    private String searchParam;
    private String corpCifNo;
    private FilterAutoFinanceAccountListingEnum viewByIndicator;
}