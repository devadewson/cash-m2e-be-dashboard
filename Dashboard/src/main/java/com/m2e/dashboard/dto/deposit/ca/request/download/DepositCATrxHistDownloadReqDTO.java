package com.m2e.dashboard.dto.deposit.ca.request.download;

import com.m2e.dashboard.dto.base.DownloadReq;
import com.m2e.dashboard.dto.deposit.DepositTrxHistoryFilterReqDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class DepositCATrxHistDownloadReqDTO extends DownloadReq {
    private String acctNumber;
    private String trxCode;
    private String currency;
    private String searchParam;
    private String[] headers;
    private DepositTrxHistoryFilterReqDTO filters;
}
