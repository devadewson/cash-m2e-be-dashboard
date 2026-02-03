package com.m2e.dashboard.jms.deposit.ca.request.account.activityinquiry;

import com.m2e.dashboard.dto.deposit.DepositTrxHistoryFilterReqDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ESBDepositCAAcctActivityReqDTO {
    private String accountNo;
    private String trxCode;
    private String searchParam;
    private String currency;
    private DepositTrxHistoryFilterReqDTO filters;
    private String mbbLastKey;
    private int page;
    private int pageSize;
}
