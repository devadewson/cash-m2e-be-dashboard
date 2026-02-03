package com.m2e.dashboard.dto.deposit.fd.request;

import com.m2e.dashboard.dto.deposit.DepositTrxHistoryFilterReqDTO;
import com.m2e.dashboard.dto.common.BasePageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class DepositFDTrxHistoryReqDTO extends BasePageRequest {
    private String acctNumber;
    private String searchParam;
    private DepositTrxHistoryFilterReqDTO filters;
}
