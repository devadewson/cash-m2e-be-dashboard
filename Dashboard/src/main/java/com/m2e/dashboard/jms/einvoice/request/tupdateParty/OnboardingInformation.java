package com.m2e.dashboard.jms.einvoice.request.tupdateParty;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class OnboardingInformation{

	@JsonProperty("approvalStatus")
	private ApprovalStatus approvalStatus;

	@JsonProperty("onboardingStatus")
	private OnboardingStatus onboardingStatus;

	@JsonProperty("onboardingType")
	private OnboardingType onboardingType;
}