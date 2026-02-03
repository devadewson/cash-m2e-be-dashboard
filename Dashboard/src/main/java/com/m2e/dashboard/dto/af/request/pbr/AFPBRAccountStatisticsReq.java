package com.m2e.dashboard.dto.af.request.pbr;

import lombok.Data;

import java.io.Serializable;

@Data
public class AFPBRAccountStatisticsReq implements Serializable {
    private String corpId;
    private String userId;
    private String provider;
    private Boolean realTimeIndicator;
}
