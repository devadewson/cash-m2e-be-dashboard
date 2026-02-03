package com.m2e.dashboard.dto.deposit.va.request;

import com.m2e.dashboard.dto.base.AccountListReq;
import lombok.Data;
import lombok.EqualsAndHashCode;


@EqualsAndHashCode(callSuper = true)
@Data
public class DepositVAAccSearchReqDTO extends AccountListReq {
    private String virtualAcctNumber;
    private String corporateCode;
}
