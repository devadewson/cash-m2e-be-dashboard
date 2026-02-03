package com.m2e.dashboard.jms.einvoice.response.tviewParty;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Address{

	@JsonProperty("country")
	private BaseCode country;

	@JsonProperty("town")
	private String town;

	@JsonProperty("postalCode")
	private String postalCode;

	@JsonProperty("format")
	private String format;

	@JsonProperty("state")
	private BaseCode state;

	@JsonProperty("line2")
	private String line2;

	@JsonProperty("line1")
	private String line1;

	@JsonProperty("line3")
	private String line3;

	@JsonProperty("line4")
	private String line4;
}