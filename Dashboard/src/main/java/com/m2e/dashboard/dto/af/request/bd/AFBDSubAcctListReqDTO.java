package com.m2e.dashboard.dto.af.request.bd;

import com.m2e.dashboard.dto.common.BasePageRequest;
import com.m2e.dashboard.enums.AccountLevelEnum;
import lombok.Data;

@Data
public class AFBDSubAcctListReqDTO extends BasePageRequest {
    private String acctNumber;
    private String searchValue;
    private AccountLevelEnum accountLevel;
}
