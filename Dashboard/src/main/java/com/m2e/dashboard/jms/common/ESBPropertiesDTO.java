package com.m2e.dashboard.jms.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class ESBPropertiesDTO implements Serializable {
    @JsonProperty("esb-ver")
    private String esbVer;

    @JsonProperty("esb-svcid")
    private String esbSvcId;

    @JsonProperty("esb-txncode")
    private String esbTxnCode;

    @JsonProperty("esb-env")
    private String esbEnv;

    @JsonProperty("esb-msgid")
    private String esbMsgId;
}
