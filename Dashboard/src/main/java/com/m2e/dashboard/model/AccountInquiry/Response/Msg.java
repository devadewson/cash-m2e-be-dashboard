package com.m2e.dashboard.model.AccountInquiry.Response;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Msg {
    @JsonProperty("MsgHeader")
    private MsgHeader msgHeader;

    @JsonProperty("MsgBody")
    private MsgBody msgBody;

    public MsgHeader getMsgHeader() {
        return msgHeader;
    }

    public MsgBody getMsgBody() {
        return msgBody;
    }

    public void setMsgBody(MsgBody msgBody) {
        this.msgBody = msgBody;
    }

    public void setMsgHeader(MsgHeader msgHeader) {
        this.msgHeader = msgHeader;

    }
}
