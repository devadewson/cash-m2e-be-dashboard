package com.m2e.dashboard.jms.einvoice.request.tviewParty;

import lombok.Data;

@Data
public class LocalCIFReq {
  private String countryOfOnboarding;
  private String sourceSystem;
  private String cifNumber;
}
