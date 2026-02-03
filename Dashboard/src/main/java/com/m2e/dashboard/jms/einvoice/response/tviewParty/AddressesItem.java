package com.m2e.dashboard.jms.einvoice.response.tviewParty;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class AddressesItem{

	@JsonProperty("identifier")
	private int identifier;

	@JsonProperty("address")
	private Address address;

	@JsonProperty("addressType")
	private BaseCode addressType;

	@JsonProperty("ucif")
	private Ucif ucif;
}