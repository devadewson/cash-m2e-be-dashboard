package com.m2e.dashboard.dto.deposit.va.request;

import com.m2e.dashboard.dto.common.BasePageRequest;
import com.m2e.dashboard.dto.deposit.DepositTrxHistoryFilterReqDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;


@EqualsAndHashCode(callSuper = true)
@Data
public class DepositVATransHistSearchReqDTO extends BasePageRequest {
    private String virtualAcctNumber;
    private String trxCode;
    private String searchParam;
    private String parentAcctNumber;
    private String parentAcctCcy;

    private DepositTrxHistoryFilterReqDTO filters;

}
