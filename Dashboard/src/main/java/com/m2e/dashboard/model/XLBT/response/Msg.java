package com.m2e.dashboard.model.XLBT.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Msg {
    @JsonProperty("MsgHeader")
    private MsgHeader msgHeader;

    @JsonProperty("MsgBody")
    private MsgBody msgBody;

    public MsgHeader getMsgHeader() {
        return msgHeader;
    }

    public void setMsgHeader(MsgHeader msgHeader) {
        this.msgHeader = msgHeader;
    }

    public MsgBody getMsgBody() {
        return msgBody;
    }

    public void setMsgBody(MsgBody msgBody) {
        this.msgBody = msgBody;
    }
}
