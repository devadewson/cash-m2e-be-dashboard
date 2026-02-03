package com.m2e.dashboard.jms.einvoice.response.tviewParty;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Geographics{

	@JsonProperty("countryOfIncorporation")
	private BaseCode countryOfIncorporation;

	@JsonProperty("countrySourceOfFunds")
	private BaseCode countrySourceOfFunds;

	@JsonProperty("residentStatus")
	private BaseCode residentStatus;

	@JsonProperty("countryOfBirth")
	private BaseCode countryOfBirth;

	@JsonProperty("citizenship")
	private BaseCode citizenship;

	@JsonProperty("countryOfPermanentResidence")
	private BaseCode countryOfPermanentResidence;
}