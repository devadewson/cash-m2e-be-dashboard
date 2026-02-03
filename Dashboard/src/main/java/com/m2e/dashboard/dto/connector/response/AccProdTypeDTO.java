package com.m2e.dashboard.dto.connector.response;

import lombok.Data;


@Data
public class AccProdTypeDTO {
    private String id;
    private String accountTypeCode;
    private String accountTypeDescription;
    private String verifyHost;
    private String provider;
    private String channelRealTime;
    private String channelPastDate;

}
