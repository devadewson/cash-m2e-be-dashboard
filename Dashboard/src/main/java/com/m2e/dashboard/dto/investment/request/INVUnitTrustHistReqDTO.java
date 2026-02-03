package com.m2e.dashboard.dto.investment.request;

import lombok.Data;


@Data
public class INVUnitTrustHistReqDTO {

    private String acctNumber;
    private String searchParam;
    private int page;
    private int pageSize;
    private INVUnitTrustHistFilterDTO filters;
}
