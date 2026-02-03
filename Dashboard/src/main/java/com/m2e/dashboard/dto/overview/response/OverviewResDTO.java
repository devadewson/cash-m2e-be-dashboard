package com.m2e.dashboard.dto.overview.response;

import lombok.Data;

import java.util.List;

@Data
public class OverviewResDTO {
    private String id;
    private String title;
    private String productAccType;
    private List<OverviewSubResDTO> account;
    private String lastUpdatedDate;
}
