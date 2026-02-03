package com.m2e.dashboard.jms.einvoice.response.tviewParty;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class Demographics{

	@JsonProperty("dateOfIncorporation")
	private String dateOfIncorporation;

	@JsonProperty("legalStructure")
	private BaseCode legalStructure;

	@JsonProperty("entityType")
	private BaseCode entityType;

	@JsonProperty("partyClass")
	private BaseCode partyClass;

	@JsonProperty("businessSize")
	private BaseCode businessSize;

	@JsonProperty("partyName")
	private PartyName partyName;

	@JsonProperty("ids")
	private List<IdsItem> ids;

	@JsonProperty("partyGroup")
	private BaseCode partyGroup;

	@JsonProperty("partyClassLegacy")
	private BaseCode partyClassLegacy;

	@JsonProperty("race")
	private BaseCode race;

	@JsonProperty("gender")
	private BaseCode gender;

	@JsonProperty("dateOfBirth")
	private String dateOfBirth;

	@JsonProperty("religion")
	private BaseCode religion;

	@JsonProperty("salutation")
	private BaseCode salutation;

	@JsonProperty("maritalStatus")
	private BaseCode maritalStatus;
}