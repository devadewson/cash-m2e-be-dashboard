package com.m2e.dashboard.sp.af.fs.request;

import com.m2e.dashboard.dto.common.BasePageRequest;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AFFSSubAcctListSpParam extends BasePageRequest {
    private String mainAcctNo;
    private String searchParam;
}