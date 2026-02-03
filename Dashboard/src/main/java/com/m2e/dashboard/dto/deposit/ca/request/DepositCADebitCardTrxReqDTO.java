package com.m2e.dashboard.dto.deposit.ca.request;

import com.m2e.dashboard.dto.common.BasePageRequest;
import com.m2e.dashboard.dto.deposit.DepositTrxHistoryFilterReqDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class DepositCADebitCardTrxReqDTO extends BasePageRequest {
    private String cardNumber;
    private String acctNumber;
    private String currency;
    private DepositTrxHistoryFilterReqDTO filters;
}
