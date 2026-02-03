package com.m2e.dashboard.jms.einvoice.response.tviewParty;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class Party{

	@JsonProperty("addresses")
	private List<AddressesItem> addresses;

	@JsonProperty("roles")
	private List<BaseCode> roles;

	@JsonProperty("employmentDetails")
	private EmploymentDetails employmentDetails;

	@JsonProperty("otherCountriesOfRelationship")
	private OtherCountriesOfRelationship otherCountriesOfRelationship;

	@JsonProperty("type")
	private String type;

	@JsonProperty("additionalDetails")
	private AdditionalDetails additionalDetails;

	@JsonProperty("onboardingInformation")
	private OnboardingInformation onboardingInformation;

	@JsonProperty("noOfProducts")
	private int noOfProducts;

	@JsonProperty("products")
	private List<ProductsItem> products;

	@JsonProperty("contactInformation")
	private ContactInformation contactInformation;

	@JsonProperty("partyTags")
	private PartyTags partyTags;

	@JsonProperty("geographics")
	private Geographics geographics;

	@JsonProperty("crsQuestions")
	private CrsQuestions crsQuestions;

	@JsonProperty("partySegmentation")
	private PartySegmentation partySegmentation;

	@JsonProperty("fatca")
	private Fatca fatca;

	@JsonProperty("ucif")
	private Ucif ucif;

	@JsonProperty("localCIF")
	private LocalCIF localCIF;

	@JsonProperty("complianceTag")
	private ComplianceTag complianceTag;

	@JsonProperty("demographics")
	private Demographics demographics;
}