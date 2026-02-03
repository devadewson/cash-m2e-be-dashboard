package com.m2e.dashboard.jms.einvoice.response.tviewParty;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ProductCode{

	@JsonProperty("code")
	private String code;

	@JsonProperty("value")
	private String value;
}