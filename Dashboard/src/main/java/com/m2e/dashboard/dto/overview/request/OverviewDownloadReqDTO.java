package com.m2e.dashboard.dto.overview.request;

import com.m2e.dashboard.dto.base.DownloadReq;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;


@Data
public class OverviewDownloadReqDTO extends DownloadReq {
    private String corpId;
    private String userId;
    private String corpCifNo;
}
