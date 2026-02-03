package com.m2e.dashboard.jms.einvoice.response.tviewParty;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class MsgHeader{

	@JsonProperty("StatusDesc")
	private String statusDesc;

	@JsonProperty("E2ETime")
	private String e2ETime;

	@JsonProperty("Ver")
	private String ver;

	@JsonProperty("SvcID")
	private String svcID;

	@JsonProperty("TxnCode")
	private String txnCode;

	@JsonProperty("Env")
	private String env;

	@JsonProperty("StatusCode")
	private String statusCode;

	@JsonProperty("MsgID")
	private String msgID;

	@JsonProperty("AdditionalStatusCodes")
	private List<AdditionalStatusCodesItem> additionalStatusCodes;
}