package com.m2e.dashboard.jms.einvoice.response.tviewParty;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CorporateContactsItem{

	@JsonProperty("identifier")
	private int identifier;

	@JsonProperty("phoneType")
	private BaseCode phoneType;

	@JsonProperty("emailAddress")
	private String emailAddress;

	@JsonProperty("otherId")
	private OtherId otherId;

	@JsonProperty("phone")
	private Phone phone;

	@JsonProperty("name")
	private String name;

	@JsonProperty("designation")
	private String designation;

	@JsonProperty("salutaion")
	private BaseCode salutaion;

	@JsonProperty("ucif")
	private Ucif ucif;
}