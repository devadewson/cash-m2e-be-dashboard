package com.m2e.dashboard.sp.af.bd.request;

import com.m2e.dashboard.dto.common.BasePageRequest;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AFBDAcctListSpParam extends BasePageRequest {
    private String userId;
    private String corpId;
    private String corpCifNo;
    private String searchParam;
}
