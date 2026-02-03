package com.m2e.dashboard.jms.common.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class ESBDTRangeReqDTO implements Serializable {
    @JsonProperty("StartDt")
    private Integer startDate;
    @JsonProperty("EndDt")
    private Integer endDate;
    @JsonProperty("StartTm")
    private Integer startTime;
    @JsonProperty("EndTm")
    private Integer endTime;
}
