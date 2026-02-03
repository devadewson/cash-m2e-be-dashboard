package com.m2e.dashboard.jms.einvoice.request.tupdateParty;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class LocalCIF{

	@JsonProperty("cifNumber")
	private String cifNumber;

	@JsonProperty("sourceSystem")
	private String sourceSystem;

	@JsonProperty("countryOfOnboarding")
	private String countryOfOnboarding;
}