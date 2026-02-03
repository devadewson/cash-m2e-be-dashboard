package com.m2e.dashboard.sp.loan.od.request;

import com.m2e.dashboard.dto.common.BasePageRequest;
import lombok.Data;

@Data
public class LNODAcctListingSpParam extends BasePageRequest {
    private String corpId;
    private String userId;
    private String corpCifNo;
    private String searchParam;
}