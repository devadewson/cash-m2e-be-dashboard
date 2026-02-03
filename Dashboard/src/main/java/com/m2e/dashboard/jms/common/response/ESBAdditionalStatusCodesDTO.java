package com.m2e.dashboard.jms.common.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class ESBAdditionalStatusCodesDTO implements Serializable {
    @JsonProperty("HostTxncode")
    private String hostTxnCode;

    @JsonProperty("HostTxndesc")
    private String hostTxnDesc;

    @JsonProperty("HostStatusCode")
    private String hostStatusCode;

    @JsonProperty("HostStatusDesc")
    private String hostStatusDesc;

    @JsonProperty("HostProductSystem")
    private String hostProductSystem;

    @JsonProperty("TXNTime")
    private String tXNTime;
}
