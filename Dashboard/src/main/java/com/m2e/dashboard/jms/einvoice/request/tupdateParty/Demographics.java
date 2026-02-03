package com.m2e.dashboard.jms.einvoice.request.tupdateParty;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Demographics{

	@JsonProperty("partyClass")
	private PartyClass partyClass;
}