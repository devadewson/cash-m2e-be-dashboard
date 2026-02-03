package com.m2e.dashboard.dto.loan.tlf.request;

import com.m2e.dashboard.dto.common.BasePageRequest;
import com.m2e.dashboard.dto.loan.tlf.LNTLFAccountType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
public class LNTrxHistoryReqDTO extends BasePageRequest {
    private LNTLFAccountType accountTypeIndicators;
    private String productCode;
    private String acctNumber;
    private String currency;
    private String provider;
    private String searchParam;
    private LNTrxHistoryFilterDTO filters;
    private String mbbLastKey;
}