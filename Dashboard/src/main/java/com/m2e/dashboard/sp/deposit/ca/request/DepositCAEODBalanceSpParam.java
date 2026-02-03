package com.m2e.dashboard.sp.deposit.ca.request;

import com.m2e.dashboard.dto.common.BasePageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Data
public class DepositCAEODBalanceSpParam extends BasePageRequest {
    private String acctNumber;
    private String acctCurrency;
    private int year;
    private int month;
    private String userId;
    private String corpId;
}
