package com.m2e.dashboard.jms.einvoice.request.tupdateParty;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class UpdatePartyMsgHeader{

	@JsonProperty("Ver")
	private String ver;

	@JsonProperty("SvcID")
	private String svcID;

	@JsonProperty("TxnCode")
	private String txnCode;

	@JsonProperty("Env")
	private String env;

	@JsonProperty("TxnDt")
	private String txnDt;

	@JsonProperty("MsgID")
	private String msgID;
}