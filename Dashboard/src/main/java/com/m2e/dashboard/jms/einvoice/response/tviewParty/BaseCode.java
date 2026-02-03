package com.m2e.dashboard.jms.einvoice.response.tviewParty;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class BaseCode {
  @JsonProperty("code")
  private String code;

  @JsonProperty("selectable")
  private Boolean selectable;

  @JsonProperty("value")
  private String value;
}
