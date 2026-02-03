package com.m2e.dashboard.jms.einvoice.response.tviewParty;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class ContactInformation{

	@JsonProperty("emailAddress")
	private String emailAddress;

	@JsonProperty("noOfPhones")
	private int noOfPhones;

	@JsonProperty("phones")
	private List<PhonesItem> phones;

	@JsonProperty("emailVerificationFlag")
	private boolean emailVerificationFlag;
}