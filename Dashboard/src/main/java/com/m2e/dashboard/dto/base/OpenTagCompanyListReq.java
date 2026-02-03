package com.m2e.dashboard.dto.base;

import com.m2e.dashboard.dto.common.BasePageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class OpenTagCompanyListReq extends BasePageRequest {
    private String searchParam;
    private String userId;
    private String corpId;
    private String product;
    private String subProduct;
    private String sourcePage;
}
