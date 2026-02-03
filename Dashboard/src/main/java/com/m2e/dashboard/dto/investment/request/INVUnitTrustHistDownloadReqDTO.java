package com.m2e.dashboard.dto.investment.request;

import com.m2e.dashboard.dto.base.DownloadReq;
import lombok.Data;
import lombok.EqualsAndHashCode;


@EqualsAndHashCode(callSuper = true)
@Data
public class INVUnitTrustHistDownloadReqDTO extends DownloadReq {

    private String acctNumber;
    private String searchParam;
    private INVUnitTrustHistFilterDTO filters;

}
