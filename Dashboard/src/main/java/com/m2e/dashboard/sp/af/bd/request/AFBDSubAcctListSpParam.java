package com.m2e.dashboard.sp.af.bd.request;

import com.m2e.dashboard.dto.common.BasePageRequest;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AFBDSubAcctListSpParam extends BasePageRequest {
    private String mainAcctNo;
    private String searchParam;
    private String userId;
    private String corpId;
}