package com.m2e.dashboard.dto.af.request.fs;

import com.m2e.dashboard.dto.common.BasePageRequest;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AFFSCertListReqDTO extends BasePageRequest {
    private String acctNumber;
    private String provider;
    private String searchParam;
}
