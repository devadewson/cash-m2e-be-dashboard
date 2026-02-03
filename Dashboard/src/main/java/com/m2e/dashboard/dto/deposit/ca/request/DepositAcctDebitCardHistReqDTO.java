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
public class DepositAcctDebitCardHistReqDTO extends BasePageRequest {
    private String startDate;
    private String endDate;
    private String startTime;
    private String endTime;
    private Float minAmount;
    private Float maxAmount;
    private String debitCardName;
}
