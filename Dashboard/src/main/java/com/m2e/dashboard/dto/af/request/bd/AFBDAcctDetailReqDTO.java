package com.m2e.dashboard.dto.af.request.bd;

import com.m2e.dashboard.enums.AccountLevelEnum;
import com.m2e.dashboard.enums.ProviderEnum;
import lombok.Data;

@Data
public class AFBDAcctDetailReqDTO {
    private String acctNumber;
    private String blockNumber;
    private String agreementNumber;
    private ProviderEnum provider;
    private AccountLevelEnum accountLevel;
    private String currency;
}
