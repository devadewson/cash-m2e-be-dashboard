package com.m2e.dashboard.dto.af.request.pbr.download;

import com.m2e.dashboard.dto.af.request.AFTrxHistoryFilterDTO;
import com.m2e.dashboard.dto.base.DownloadReq;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class AFPBRTrxHistoryDownloadReq extends DownloadReq {
    private String acctNumber;
    private String searchParam;
    private String[] headers; // TODO: Instead of passing headers FE, query from DB
    private AFTrxHistoryFilterDTO filters;
}