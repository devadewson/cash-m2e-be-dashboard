package com.m2e.dashboard.jms.einvoice.response.tviewParty;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class OtherCountriesOfRelationship{

	@JsonProperty("relationshipIndicator")
	private RelationshipIndicator relationshipIndicator;

	@JsonProperty("relationshipCountries")
	private List<RelationshipCountriesItem> relationshipCountries;
}