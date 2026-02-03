package com.m2e.dashboard.jms.einvoice.response.tviewParty;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class AdditionalStatusCodesItem{

	@JsonProperty("HostTxncode")
	private String hostTxncode;

	@JsonProperty("HostStatusCode")
	private String hostStatusCode;

	@JsonProperty("HostStatusDesc")
	private String hostStatusDesc;

	@JsonProperty("HostProductSystem")
	private String hostProductSystem;

	@JsonProperty("HostTxndesc")
	private String hostTxndesc;

	@JsonProperty("TXNTime")
	private String tXNTime;
}