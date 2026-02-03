package com.m2e.dashboard.jms.einvoice.request.tupdateParty;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class AdditionalDetails{

	@JsonProperty("salesServiceTaxNo")
	private String salesServiceTaxNo;
}