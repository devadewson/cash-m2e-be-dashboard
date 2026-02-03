package com.m2e.dashboard.jms.einvoice.response.tviewParty;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class EmploymentDetails{

	@JsonProperty("occupation")
	private BaseCode occupation;

	@JsonProperty("employmentType")
	private BaseCode employmentType;

	@JsonProperty("occupationSector")
	private BaseCode occupationSector;

	@JsonProperty("employerName")
	private EmployerName employerName;

	@JsonProperty("grossIncomeRange")
	private BaseCode grossIncomeRange;

}