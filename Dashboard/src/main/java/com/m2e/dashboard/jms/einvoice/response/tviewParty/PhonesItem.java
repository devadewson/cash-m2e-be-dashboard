package com.m2e.dashboard.jms.einvoice.response.tviewParty;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class PhonesItem{

	@JsonProperty("identifier")
	private int identifier;

	@JsonProperty("phoneType")
	private BaseCode phoneType;

	@JsonProperty("phone")
	private Phone phone;

	@JsonProperty("action")
	private Object action;

	@JsonProperty("primaryIndicator")
	private String primaryIndicator;

	@JsonProperty("ucif")
	private Ucif ucif;
}