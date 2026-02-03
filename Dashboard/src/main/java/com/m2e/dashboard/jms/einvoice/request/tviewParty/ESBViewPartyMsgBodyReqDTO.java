package com.m2e.dashboard.jms.einvoice.request.tviewParty;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ESBViewPartyMsgBodyReqDTO {
    @JsonProperty("Msg")
    private ViewPartyMsgReq msgReq;
}
