package com.m2e.dashboard.dto.investment.request;

import com.m2e.dashboard.dto.base.DownloadReq;
import lombok.Data;


@Data
public class INVUnitTrustFundDownloadReqDTO extends DownloadReq {
    private String acctNumber;
    private String searchValue;
}
