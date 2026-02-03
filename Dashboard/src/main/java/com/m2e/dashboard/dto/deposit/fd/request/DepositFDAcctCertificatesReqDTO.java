package com.m2e.dashboard.dto.deposit.fd.request;

import com.m2e.dashboard.dto.common.BasePageRequest;
import com.m2e.dashboard.enums.ProviderEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class DepositFDAcctCertificatesReqDTO extends BasePageRequest {
    private String acctNumber;
    private String searchParam;
    private String currency;
    private ProviderEnum provider;
}
