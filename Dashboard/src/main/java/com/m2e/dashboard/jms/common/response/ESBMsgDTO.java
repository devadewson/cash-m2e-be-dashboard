package com.m2e.dashboard.jms.common.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class ESBMsgDTO<T> implements Serializable {
    @JsonProperty("MsgHeader")
    private ESBMsgHeaderDTO msgHeader;

    @JsonProperty("MsgBody")
    private transient T msgBody;
}
