package com.m2e.dashboard.jms.einvoice.request.tupdateParty;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class OnboardingStatus{

	@JsonProperty("code")
	private String code;

	@JsonProperty("selectable")
	private String selectable;

	@JsonProperty("value")
	private String value;
}