package com.m2e.dashboard.dto.loan.tlf.request.download;

import com.m2e.dashboard.dto.base.DownloadReq;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class LNTLFAcctListingDownloadReqDTO extends DownloadReq {
    private String corpCifNo;
    private String searchParam;
}