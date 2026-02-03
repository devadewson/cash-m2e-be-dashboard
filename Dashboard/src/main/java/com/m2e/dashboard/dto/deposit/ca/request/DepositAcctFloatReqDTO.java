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
public class DepositAcctFloatReqDTO extends BasePageRequest {
    private String acctNumber;
    private String currency;
    private String expiryDate;
}
