package com.m2e.dashboard.jms.einvoice.request.tupdateParty;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Msg{

	@JsonProperty("MsgHeader")
	private UpdatePartyMsgHeader msgHeader;

	@JsonProperty("MsgBody")
	private MsgBody msgBody;
}