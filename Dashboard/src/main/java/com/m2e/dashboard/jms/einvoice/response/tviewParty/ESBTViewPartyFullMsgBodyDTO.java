package com.m2e.dashboard.jms.einvoice.response.tviewParty;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class ESBTViewPartyFullMsgBodyDTO {

	@JsonProperty("addresses")
	private List<AddressesItem> addresses;

	@JsonProperty("connectedParties")
	private List<ConnectedPartiesItem> connectedParties;

	@JsonProperty("partyFinancials")
	private List<PartyFinancialsItem> partyFinancials;

	@JsonProperty("roles")
	private List<BaseCode> roles;

	@JsonProperty("crsList")
	private List<CrsListItem> crsList;

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

	@JsonProperty("partyClass")
	private PartyClass partyClass;

	@JsonProperty("geographics")
	private Geographics geographics;

	@JsonProperty("crsQuestions")
	private CrsQuestions crsQuestions;

	@JsonProperty("partySegmentation")
	private PartySegmentation partySegmentation;

	@JsonProperty("corporateContacts")
	private List<CorporateContactsItem> corporateContacts;

	@JsonProperty("countriesOfOperation")
	private List<CountriesOfOperationItem> countriesOfOperation;

	@JsonProperty("fatca")
	private Fatca fatca;

	@JsonProperty("ucif")
	private Ucif ucif;

	@JsonProperty("localCIF")
	private LocalCIF localCIF;

	@JsonProperty("complianceTag")
	private ComplianceTag complianceTag;

	@JsonProperty("countryOfOnboarding")
	private String countryOfOnboarding;

	@JsonProperty("demographics")
	private Demographics demographics;
}