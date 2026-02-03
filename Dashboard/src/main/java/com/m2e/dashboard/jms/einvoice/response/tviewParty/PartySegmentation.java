package com.m2e.dashboard.jms.einvoice.response.tviewParty;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class PartySegmentation{

	@JsonProperty("isPWMCustomer")
	private boolean isPWMCustomer;

	@JsonProperty("managedByGBCentral")
	private boolean managedByGBCentral;

	@JsonProperty("marketSubSubSegment")
	private BaseCode marketSubSubSegment;

	@JsonProperty("marketSubSegment")
	private BaseCode marketSubSegment;

	@JsonProperty("serviceSegment")
	private BaseCode serviceSegment;

	@JsonProperty("marketSegment")
	private BaseCode marketSegment;

	@JsonProperty("customerSegment")
	private BaseCode customerSegment;

	@JsonProperty("manualClaimFlag")
	private boolean manualClaimFlag;
}