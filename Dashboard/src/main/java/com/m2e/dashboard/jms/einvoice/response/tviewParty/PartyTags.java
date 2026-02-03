package com.m2e.dashboard.jms.einvoice.response.tviewParty;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class PartyTags{

	@JsonProperty("isDeceased")
	private boolean isDeceased;

	@JsonProperty("isStaff")
	private boolean isStaff;

	@JsonProperty("insiderCode")
	private Object insiderCode;

	@JsonProperty("deceasedDate")
	private Object deceasedDate;

	@JsonProperty("isVip")
	private Object isVip;

	@JsonProperty("interCompanyEntityTag")
	private Object interCompanyEntityTag;
}