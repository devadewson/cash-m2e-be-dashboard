package com.m2e.dashboard.dto.af.request.hp.download;

import com.m2e.dashboard.dto.af.request.AFTrxHistoryFilterDTO;
import com.m2e.dashboard.dto.base.DownloadReq;
import com.m2e.dashboard.enums.ProviderEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "Request for Hire Purchase transaction history download", allOf = {DownloadReq.class})
public class AFHPTrxHistDownloadReq extends DownloadReq {
    private String acctNumber;
    private ProviderEnum provider;
    private String searchParam;
    private String[] headers;
    private AFTrxHistoryFilterDTO filters;
}
