package com.m2e.dashboard.jms.common.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class ESBMsgHeaderDTO implements Serializable {

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

    @JsonProperty("StatusCode")
    private String statusCode;

    @JsonProperty("StatusDesc")
    private String statusDesc;

    @JsonProperty("E2ETime")
    private String e2ETime;

    @JsonProperty("AdditionalStatusCodes")
    private List<ESBAdditionalStatusCodesDTO> additionalStatusCodes;
}
