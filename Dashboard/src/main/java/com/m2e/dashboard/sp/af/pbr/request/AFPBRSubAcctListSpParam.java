package com.m2e.dashboard.sp.af.pbr.request;

import com.m2e.dashboard.dto.common.BasePageRequest;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AFPBRSubAcctListSpParam extends BasePageRequest {
    private String mainAcctNo;
    private String searchParam;
    private String userId;
    private String corpId;
}