package com.m2e.dashboard.dto.af.request.hp;

import com.m2e.dashboard.dto.af.request.AFTrxHistoryFilterDTO;
import com.m2e.dashboard.dto.common.BasePageRequest;
import com.m2e.dashboard.enums.ProviderEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class AFHPTrxHistReqDTO extends BasePageRequest {
    private String acctNumber;
    private String searchParam;
    private ProviderEnum provider;
    private AFTrxHistoryFilterDTO filters;
    private String currency;
    private String mbbLastKey;
}
