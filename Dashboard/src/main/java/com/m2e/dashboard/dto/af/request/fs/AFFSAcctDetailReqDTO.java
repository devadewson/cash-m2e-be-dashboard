package com.m2e.dashboard.dto.af.request.fs;

import com.m2e.dashboard.enums.AccountLevelEnum;
import com.m2e.dashboard.enums.ProviderEnum;
import lombok.Data;

@Data
public class AFFSAcctDetailReqDTO {
    private String acctNumber;
    private String certNo;
    private String currency;
    private ProviderEnum provider;
    private AccountLevelEnum accountLevel;
}