package com.m2e.dashboard.jms.einvoice.response.tviewParty;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CrsListItem{

	@JsonProperty("taxRegistredCountry")
	private BaseCode taxRegistredCountry;

	@JsonProperty("identifier")
	private int identifier;

	@JsonProperty("reasonForNoTin")
	private BaseCode reasonForNoTin;

	@JsonProperty("ucif")
	private Ucif ucif;

	@JsonProperty("taxId")
	private String taxId;

}