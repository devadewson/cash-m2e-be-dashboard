package com.m2e.dashboard.jms.einvoice.response.tviewParty;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Phone{

	@JsonProperty("areaCode")
	private String areaCode;

	@JsonProperty("extension")
	private Object extension;

	@JsonProperty("phoneNumber")
	private String phoneNumber;

	@JsonProperty("countryCode")
	private String countryCode;

	@JsonProperty("displayCountryCode")
	private BaseCode displayCountryCode;

	@JsonProperty("verificationFlag")
	private boolean verificationFlag;
}