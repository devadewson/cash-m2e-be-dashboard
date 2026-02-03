package com.m2e.dashboard.dto.deposit.va.request;

import com.m2e.dashboard.dto.base.DownloadReq;
import lombok.Data;
import lombok.EqualsAndHashCode;


@EqualsAndHashCode(callSuper = true)
@Data
public class DepositVADownloadReqDTO extends DownloadReq {
    private String currAcctNumber;
    private String virtualAcctNumber;
    private String corporateCode;

    private String corpCifNo;
}
