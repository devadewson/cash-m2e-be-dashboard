package com.m2e.dashboard.jms.einvoice.request.tviewParty;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ViewPartyMsgReq {

  @JsonProperty("MsgHeader")
  private MsgHeader MsgHeader;

  @JsonProperty("MsgBody")
  private Object MsgBody;
}
