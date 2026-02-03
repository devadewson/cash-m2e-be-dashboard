package com.m2e.dashboard.dto.af.request.pbr.download;

import com.m2e.dashboard.dto.base.DownloadReq;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class AFPBRSubaccountListingDownloadReq extends DownloadReq {
    private String acctNumber;
    private String searchParam;
}
