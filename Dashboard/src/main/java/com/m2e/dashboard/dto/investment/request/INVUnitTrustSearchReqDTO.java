package com.m2e.dashboard.dto.investment.request;

import lombok.Data;


@Data
public class INVUnitTrustSearchReqDTO {
    private String corpId;
    private String searchValue;
    private int page;
    private int pageSize;
}
