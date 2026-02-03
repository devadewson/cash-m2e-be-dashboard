package com.m2e.dashboard.jms.einvoice.request.tupdateParty;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CrsListItem{

	@JsonProperty("taxRegistredCountry")
	private TaxRegistredCountry taxRegistredCountry;

	@JsonProperty("action")
	private String action;

	@JsonProperty("taxId")
	private String taxId;

	@JsonProperty("identifier")
	private int identifier;
}