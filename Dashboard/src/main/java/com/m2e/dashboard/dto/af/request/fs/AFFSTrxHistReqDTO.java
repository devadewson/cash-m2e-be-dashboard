package com.m2e.dashboard.dto.af.request.fs;

import com.m2e.dashboard.dto.af.request.AFTrxHistoryFilterDTO;
import com.m2e.dashboard.dto.common.BasePageRequest;
import com.m2e.dashboard.enums.ProviderEnum;
import lombok.Data;

@Data
public class AFFSTrxHistReqDTO extends BasePageRequest {
    private String acctNumber;
    private String currency;
    private String searchParam;
    private String provider;
    private AFTrxHistoryFilterDTO filters;
}