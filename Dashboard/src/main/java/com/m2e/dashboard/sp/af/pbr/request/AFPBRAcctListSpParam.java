package com.m2e.dashboard.sp.af.pbr.request;

import com.m2e.dashboard.dto.common.BasePageRequest;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AFPBRAcctListSpParam extends BasePageRequest {
    private String userId;
    private String corpId;
    private String corpCifNo;
    private String searchParam;
}
