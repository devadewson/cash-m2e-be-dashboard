package com.m2e.dashboard.sp.common.request;

import lombok.Data;

@Data
public class SearchResultCountAFSpParam {
    private String userId;
    private String corpId;
    private String viewByIndicator;
    private String searchParam;
    private String subProduct;
    private String corpCifNo;
}
