package com.m2e.dashboard.dto.investment.request;

import lombok.Data;

@Data
public class INVUnitTrustHistFilterDTO {
    private String startDate;
    private String endDate;
    private String fundName;
}
