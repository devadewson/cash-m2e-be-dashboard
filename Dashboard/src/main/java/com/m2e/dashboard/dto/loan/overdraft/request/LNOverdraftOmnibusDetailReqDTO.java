package com.m2e.dashboard.dto.loan.overdraft.request;

import com.m2e.dashboard.dto.common.BasePageRequest;
import lombok.Data;

@Data
public class LNOverdraftOmnibusDetailReqDTO extends BasePageRequest {
    private String acctNumber;
}