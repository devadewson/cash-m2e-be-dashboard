package com.m2e.dashboard.jms.af.common;

import com.m2e.dashboard.dto.af.request.AFTrxHistoryFilterDTO;
import com.m2e.dashboard.enums.ProviderEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ESBAFAcctActivityReqDTO {
    private String acctNumber;
    private String searchParam;
    private String trxCode;
    private ProviderEnum provider;
    private String currency;
    private String mbbLastKey;
    private String acctType;
    private AFTrxHistoryFilterDTO filters;
    private int page;
    private int pageSize;
}
