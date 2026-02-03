package com.m2e.dashboard.dto.af.request.bd;

import com.m2e.dashboard.dto.af.request.AFTrxHistoryFilterDTO;
import com.m2e.dashboard.dto.common.BasePageRequest;
import com.m2e.dashboard.enums.ProviderEnum;
import lombok.Data;

@Data
public class AFBDTrxHistoryReqDTO extends BasePageRequest {
    private String acctNumber;
    private String searchParam;
    private String trxCode;
    private ProviderEnum provider;
    private String currency;
    private String mbbLastKey;
    private AFTrxHistoryFilterDTO filters;
}
