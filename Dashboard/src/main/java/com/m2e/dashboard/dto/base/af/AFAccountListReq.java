package com.m2e.dashboard.dto.base.af;

import com.m2e.dashboard.dto.common.BasePageRequest;
import com.m2e.dashboard.enums.FilterAutoFinanceAccountListingEnum;
import com.m2e.dashboard.enums.ProviderEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AFAccountListReq extends BasePageRequest {
    private boolean realTimeIndicator;
    private String searchParam;
    private String corpCifNo;
    private FilterAutoFinanceAccountListingEnum viewByIndicator;
}