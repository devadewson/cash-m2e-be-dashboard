package com.m2e.dashboard.jms.einvoice.request.tviewParty;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class MsgHeader {
  @JsonProperty("MsgID")
  private String MsgID;
  @JsonProperty("Ver")
  private String Ver;
  @JsonProperty("SvcID")
  private String SvcID;
  @JsonProperty("TxnCode")
  private String TxnCode;
  @JsonProperty("Env")
  private String Env;
}
