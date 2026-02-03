package com.m2e.dashboard.dto.deposit.fd.request.download;

import com.m2e.dashboard.dto.deposit.DepositTrxHistoryFilterReqDTO;
import com.m2e.dashboard.dto.base.DownloadReq;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class DepositFDTrxHistoryDownloadReqDTO extends DownloadReq {
    private String acctNumber;
    private String searchParam;
    private String[] headers;
    private DepositTrxHistoryFilterReqDTO filters;
}
