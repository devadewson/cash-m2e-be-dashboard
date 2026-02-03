package com.m2e.dashboard.dto.investment.request;

import com.m2e.dashboard.dto.base.DownloadReq;
import lombok.Data;


@Data
public class INVUnitTrustAccDownloadReqDTO extends DownloadReq {
    private String searchParam;
    private String corpId;
    private String userId;
    private String corpCifNo;
}
