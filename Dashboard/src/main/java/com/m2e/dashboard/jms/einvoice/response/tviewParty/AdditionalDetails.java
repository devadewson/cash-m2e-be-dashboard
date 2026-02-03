package com.m2e.dashboard.jms.einvoice.response.tviewParty;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class AdditionalDetails{

	@JsonProperty("isLargeCompany")
	private boolean isLargeCompany;

	@JsonProperty("preferredLanguage")
	private BaseCode preferredLanguage;

	@JsonProperty("industrySector")
	private BaseCode industrySector;

	@JsonProperty("salesServiceTaxNo")
	private String salesServicesTaxNo;

	@JsonProperty("mgsic")
	private BaseCode mgsic;

	@JsonProperty("noOfFullTimeEmployees")
	private int noOfFullTimeEmployees;

	@JsonProperty("education")
	private BaseCode education;

	@JsonProperty("totalFinancialAssets")
	private String totalFinancialAssets;

	@JsonProperty("consentForMarketing")
	private boolean consentForMarketing;
}