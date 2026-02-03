package com.m2e.dashboard.sp.loan.rc.request;

import com.m2e.dashboard.dto.common.BasePageRequest;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LNRCAcctListingSpParam extends BasePageRequest {
    private String corpId;
    private String userId;
    private String corpCifNo;
    private String searchParam;
}
