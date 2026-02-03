package com.m2e.dashboard.jms.einvoice.request.tviewParty;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class MsgDetail {

  private String transactionType;
  private String requestor;
  private String prefferedDateFormat;
  private String countryOfOnboarding;
  // Size of searchCriteria
  private Integer noOfOccurence;
  private Integer pageNo = 0;
  private Integer maxResult = 25;
  private List<SearchCriteriaDet> searchCriteria = new ArrayList<>();
}
