package com.m2e.dashboard.dto.af.request.bd.download;

import com.m2e.dashboard.dto.base.DownloadReq;
import com.m2e.dashboard.enums.FilterAutoFinanceAccountListingEnum;
import com.m2e.dashboard.enums.ProviderEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class AFBDAcctListDownloadReqDTO extends DownloadReq {
    private String searchParam;
    private String corpCifNo;

    private FilterAutoFinanceAccountListingEnum viewByIndicator;
}
