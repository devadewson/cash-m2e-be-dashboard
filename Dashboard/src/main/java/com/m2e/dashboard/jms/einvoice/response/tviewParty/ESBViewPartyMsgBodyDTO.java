package com.m2e.dashboard.jms.einvoice.response.tviewParty;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ESBViewPartyMsgBodyDTO {
    @JsonProperty("Msg")
    private Msg msg;
}
