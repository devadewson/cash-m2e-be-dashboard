package com.m2e.dashboard.jms.einvoice.response.tviewParty;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class LocalCIF{

	@JsonProperty("cifNumber")
	private String cifNumber;

	@JsonProperty("sourceSystem")
	private String sourceSystem;

	@JsonProperty("convertedFlag")
	private boolean convertedFlag;

	@JsonProperty("countryOfOnboarding")
	private String countryOfOnboarding;
}