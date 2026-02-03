package com.m2e.dashboard.jms.einvoice.request.tviewParty;

import lombok.Data;

@Data
public class DetailReq {
  private String transactionType;
  private String requestor;
  private String prefferedDateFormat = "dd/MM/yyyy";
  private String countryOfOnboarding = "MYS";
  private PartyClassReq partyClass;
  private UniversalCIF universalCIF;
  private LocalCIFReq localCIF;
}
