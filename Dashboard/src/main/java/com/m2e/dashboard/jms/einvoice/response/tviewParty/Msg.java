package com.m2e.dashboard.jms.einvoice.response.tviewParty;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Msg{

	@JsonProperty("MsgHeader")
	private MsgHeader msgHeader;

	@JsonProperty("MsgBody")
	private ESBTViewPartyFullMsgBodyDTO msgBody;

	public void setMsgHeader(MsgHeader msgHeader){
		this.msgHeader = msgHeader;
	}

	public MsgHeader getMsgHeader(){
		return msgHeader;
	}

	public void setMsgBody(ESBTViewPartyFullMsgBodyDTO msgBody){
		this.msgBody = msgBody;
	}

	public ESBTViewPartyFullMsgBodyDTO getMsgBody(){
		return msgBody;
	}
}