package com.m2e.dashboard.jms.einvoice.response.tviewParty;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigInteger;

@Data
public class PartyFinancialsItem{

	@JsonProperty("annualSalesTurnover")
	private BigInteger annualSalesTurnover;

	@JsonProperty("authorizedCapital")
	private BigInteger authorizedCapital;

	@JsonProperty("paidUpCapital")
	private BigInteger paidUpCapital;

	@JsonProperty("financialperiod")
	private BaseCode financialperiod;
}