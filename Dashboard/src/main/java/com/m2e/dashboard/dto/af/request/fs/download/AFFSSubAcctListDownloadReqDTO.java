package com.m2e.dashboard.dto.af.request.fs.download;

import com.m2e.dashboard.dto.base.DownloadReq;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class AFFSSubAcctListDownloadReqDTO extends DownloadReq {
    private String acctNumber;
    private String provider;
    private String searchParam;
}