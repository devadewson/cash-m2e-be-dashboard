package com.m2e.dashboard.jms.einvoice.response.tviewParty;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CountriesOfOperationItem{

	@JsonProperty("identifier")
	private int identifier;

	@JsonProperty("isPrimary")
	private boolean isPrimary;

	@JsonProperty("operationStartDate")
	private String operationStartDate;

	@JsonProperty("countriesOfOperation")
	private BaseCode countriesOfOperation;

	@JsonProperty("ucif")
	private Ucif ucif;
}