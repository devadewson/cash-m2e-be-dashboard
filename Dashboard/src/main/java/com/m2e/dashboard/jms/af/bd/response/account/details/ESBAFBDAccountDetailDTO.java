package com.m2e.dashboard.jms.af.bd.response.account.details;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class ESBAFBDAccountDetailDTO implements Serializable {
    @JsonProperty("Name")
    private String name;
    @JsonProperty("Value")
    private String value;
}


