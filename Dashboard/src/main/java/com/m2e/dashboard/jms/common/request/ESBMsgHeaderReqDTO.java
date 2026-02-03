package com.m2e.dashboard.jms.common.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class ESBMsgHeaderReqDTO implements Serializable {
    @JsonProperty("Ver")
    private String ver;

    @JsonProperty("SvcID")
    private String svcID;

    @JsonProperty("TxnCode")
    private String txnCode;

    @JsonProperty("Env")
    private String env;

    @JsonProperty("MsgID")
    private String msgID;
}
