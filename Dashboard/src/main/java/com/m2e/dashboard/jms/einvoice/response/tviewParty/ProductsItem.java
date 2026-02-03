package com.m2e.dashboard.jms.einvoice.response.tviewParty;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ProductsItem{

	@JsonProperty("identifier")
	private int identifier;

	@JsonProperty("productCode")
	private ProductCode productCode;

	@JsonProperty("status")
	private String status;
}