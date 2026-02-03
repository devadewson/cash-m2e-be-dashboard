package com.m2e.dashboard.jms.einvoice.response.tviewParty;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class IdsItem{

	@JsonProperty("identifier")
	private int identifier;

	@JsonProperty("primaryDocument")
	private String primaryDocument;

	@JsonProperty("idType")
	private BaseCode idType;

	@JsonProperty("universalCIF")
	private UniversalCIF universalCIF;

	@JsonProperty("idNumber")
	private String idNumber;

	@JsonProperty("idIssuedCountry")
	private BaseCode idIssuedCountry;

	@JsonProperty("idIssuedRegion")
	private BaseCode idIssuedRegion;

	@JsonProperty("idExpiryDate")
	private String idExpiryDate;
}