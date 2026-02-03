package com.m2e.dashboard.jms.einvoice.response.tviewParty;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class PartyClass{

	@JsonProperty("code")
	private String code;

	@JsonProperty("selectable")
	private boolean selectable;

	@JsonProperty("value")
	private String value;
}