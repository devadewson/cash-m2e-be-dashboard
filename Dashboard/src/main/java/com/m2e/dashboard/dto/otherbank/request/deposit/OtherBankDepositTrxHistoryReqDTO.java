package com.m2e.dashboard.dto.otherbank.request.deposit;

import com.m2e.dashboard.dto.common.BasePageRequest;
import com.m2e.dashboard.dto.otherbank.request.OtherBankTrxHistoryReqDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class OtherBankDepositTrxHistoryReqDTO extends BasePageRequest {
    private String acctNumber;
    private String trxCode;
    private String searchParam;
    private OtherBankTrxHistoryReqDTO filters;
    private String msgType;
}
