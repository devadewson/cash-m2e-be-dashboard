package com.m2e.dashboard.sp.deposit.ca.request;

import com.m2e.dashboard.dto.common.BasePageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Data
public class DepositCAPaymentHistorySpParam extends BasePageRequest {
    private String acctNumber;
    private String acctCurrency;
    private String paymentType;
    private String trxType;
    private String startDate;
    private String endDate;
    private String userId;
    private String corpId;
    private String searchParam;
}
