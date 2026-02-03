package com.m2e.dashboard.dto.af.request.hp.download;

import com.m2e.dashboard.dto.base.DownloadReq;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class AFHPAcctListDownloadReq extends DownloadReq {
    private String searchParam;
    private String openTaggedCorpId;
    private String corpCifNo;
}
