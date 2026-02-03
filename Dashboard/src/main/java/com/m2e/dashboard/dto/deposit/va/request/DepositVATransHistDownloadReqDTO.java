package com.m2e.dashboard.dto.deposit.va.request;

import com.m2e.dashboard.dto.base.DownloadReq;
import com.m2e.dashboard.dto.deposit.DepositTrxHistoryFilterReqDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;


@EqualsAndHashCode(callSuper = true)
@Data
public class DepositVATransHistDownloadReqDTO extends DownloadReq {
    private String virtualAcctNumber;
    private String trxCode;
    private String searchParam;

    private String[] headers;
    private DepositTrxHistoryFilterReqDTO filters;
}
