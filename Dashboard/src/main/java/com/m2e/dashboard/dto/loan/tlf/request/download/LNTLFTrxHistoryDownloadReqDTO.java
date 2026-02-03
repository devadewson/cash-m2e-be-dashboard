package com.m2e.dashboard.dto.loan.tlf.request.download;

import com.m2e.dashboard.dto.base.DownloadReq;
import com.m2e.dashboard.dto.loan.tlf.LNTLFAccountType;
import com.m2e.dashboard.dto.loan.tlf.request.LNTrxHistoryFilterDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class LNTLFTrxHistoryDownloadReqDTO extends DownloadReq {
    private LNTLFAccountType accountTypeIndicators;
    private String productCode;
    private String acctNumber;
    private String currency;
    private String provider;
    private String[] headers;
    private String searchParam;
    private LNTrxHistoryFilterDTO filter;
}