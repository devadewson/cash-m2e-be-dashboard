package com.m2e.dashboard.jms.einvoice.response.tviewParty;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CrsQuestions{

	@JsonProperty("otherJurisdictionTelephone")
	private boolean otherJurisdictionTelephone;

	@JsonProperty("identifier")
	private int identifier;

	@JsonProperty("otherJurisdictionHoldAddress")
	private boolean otherJurisdictionHoldAddress;

	@JsonProperty("ucif")
	private Ucif ucif;

	@JsonProperty("otherJurisdictionAccounts")
	private boolean otherJurisdictionAccounts;

	@JsonProperty("otherJurisdiction")
	private boolean otherJurisdiction;

	@JsonProperty("otherJurisdictionAddress")
	private boolean otherJurisdictionAddress;

	@JsonProperty("otherJurisdictionPOA")
	private boolean otherJurisdictionPOA;

	@JsonProperty("status")
	private BaseCode status;
}