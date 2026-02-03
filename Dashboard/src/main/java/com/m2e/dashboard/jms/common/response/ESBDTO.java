package com.m2e.dashboard.jms.common.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class ESBDTO implements Serializable {
    @JsonProperty("Msg")
    private ESBMsgDTO message;
}
