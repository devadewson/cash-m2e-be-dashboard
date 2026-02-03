package com.m2e.dashboard.jms.common.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
@Builder
public class ESBMsgReqDTO<T> implements Serializable {
    @JsonProperty("MsgHeader")
    private ESBMsgHeaderReqDTO messageHeader;

    @JsonProperty("MsgBody")
    private transient T messageBody;
}
