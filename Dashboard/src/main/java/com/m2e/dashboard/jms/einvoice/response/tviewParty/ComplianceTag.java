package com.m2e.dashboard.jms.einvoice.response.tviewParty;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ComplianceTag{

	@JsonProperty("isSelfDeclaredFamilyPEP")
	private boolean isSelfDeclaredFamilyPEP;

	@JsonProperty("crrManualOverrideFlag")
	private boolean crrManualOverrideFlag;

	@JsonProperty("sanctionsTagging")
	private BaseCode sanctionsTagging;

	@JsonProperty("customerRiskRating")
	private BaseCode customerRiskRating;

	@JsonProperty("isSelfDeclaredPEP")
	private boolean isSelfDeclaredPEP;

	@JsonProperty("numOfWatchlistHits")
	private int numOfWatchlistHits;

	@JsonProperty("assessmentId")
	private String assessmentId;

	@JsonProperty("screeningStatus")
	private BaseCode screeningStatus;

	@JsonProperty("screeningDetails")
	private ScreeningDetails screeningDetails;

	@JsonProperty("isSanctionCountryTagged")
	private boolean isSanctionCountryTagged;
}