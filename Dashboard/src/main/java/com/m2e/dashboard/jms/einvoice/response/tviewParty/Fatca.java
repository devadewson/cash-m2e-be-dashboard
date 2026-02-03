package com.m2e.dashboard.jms.einvoice.response.tviewParty;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Fatca{

	@JsonProperty("hasPOADesignatedToUSCitizen")
	private boolean hasPOADesignatedToUSCitizen;

	@JsonProperty("hasAccountInUS")
	private boolean hasAccountInUS;

	@JsonProperty("isUSCitizen")
	private boolean isUSCitizen;

	@JsonProperty("customerConsent")
	private boolean customerConsent;

	@JsonProperty("hasProvidedUSHoldMail")
	private boolean hasProvidedUSHoldMail;

	@JsonProperty("otherDocument")
	private String otherDocument;

	@JsonProperty("isUSResident")
	private boolean isUSResident;

	@JsonProperty("isUSPermanentResident")
	private boolean isUSPermanentResident;

	@JsonProperty("w9Form")
	private String w9Form;

	@JsonProperty("hasUSContactNo")
	private boolean hasUSContactNo;

	@JsonProperty("isBornInUSOrCountryOfIncorInUS")
	private boolean isBornInUSOrCountryOfIncorInUS;

	@JsonProperty("isAddressInUS")
	private boolean isAddressInUS;

	@JsonProperty("otherDocNotProvided")
	private String otherDocNotProvided;

	@JsonProperty("entityClassification")
	private BaseCode entityClassification;

	@JsonProperty("w8BenEForm")
	private String w8BenEForm;

	@JsonProperty("isUSDesignation")
	private boolean isUSDesignation;

	@JsonProperty("status")
	private BaseCode status;
}