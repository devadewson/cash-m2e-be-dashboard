package com.m2e.dashboard.dto.deposit.ca.request;

import com.m2e.dashboard.dto.deposit.DepositTrxHistoryFilterReqDTO;
import com.m2e.dashboard.dto.common.BasePageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class DepositTrxHistoryReqDTO extends BasePageRequest {
    private String acctNumber;
    private String trxCode;
    private String searchParam;
    private String currency;
    private DepositTrxHistoryFilterReqDTO filters;
}
