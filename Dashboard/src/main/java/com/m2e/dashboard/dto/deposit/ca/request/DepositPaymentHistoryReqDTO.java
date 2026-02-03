package com.m2e.dashboard.dto.deposit.ca.request;

import com.m2e.dashboard.dto.common.BasePageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
public class DepositPaymentHistoryReqDTO extends BasePageRequest {
    private String acctNumber;
    private String currency;
    private String paymentType; // TODO: What is dropdown value
    private String trxType; // TODO: What is dropdown value
    private String startDate;
    private String endDate;
    private String searchParam;
}
