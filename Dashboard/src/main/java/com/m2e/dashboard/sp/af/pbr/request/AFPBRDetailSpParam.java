package com.m2e.dashboard.sp.af.pbr.request;

import com.m2e.dashboard.dto.common.BasePageRequest;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AFPBRDetailSpParam extends BasePageRequest {
    private String masterAcctNumber;
    private String acctNumber;
    private String corpId;
    private String userId;
}