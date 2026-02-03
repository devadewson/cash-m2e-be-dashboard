package com.m2e.dashboard.jms.einvoice.response.tviewParty;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class OnboardingInformation{

	@JsonProperty("approvalStatus")
	private BaseCode approvalStatus;

	@JsonProperty("onboardingChannel")
	private BaseCode onboardingChannel;

	@JsonProperty("onboardingStatus")
	private BaseCode onboardingStatus;

	@JsonProperty("lastUpdatedBy")
	private String lastUpdatedBy;

	@JsonProperty("lastUpdatedDate")
	private String lastUpdatedDate;

	@JsonProperty("createdDate")
	private String createdDate;

	@JsonProperty("originatingBranchCode")
	private String originatingBranchCode;

	@JsonProperty("createdBy")
	private String createdBy;

	@JsonProperty("homeBranchCode")
	private String homeBranchCode;

	@JsonProperty("onboardingType")
	private BaseCode onboardingType;

	@JsonProperty("countryOfOnboarding")
	private BaseCode countryOfOnboarding;
}