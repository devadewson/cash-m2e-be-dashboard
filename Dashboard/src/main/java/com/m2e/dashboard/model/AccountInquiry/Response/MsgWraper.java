package com.m2e.dashboard.model.AccountInquiry.Response;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MsgWraper {
    public MsgWraper() {this.msg = new Msg();}

    @JsonProperty("Msg")
    private Msg msg ;

    public Msg getMsg() {
        return msg;
    }

    public void setMsg(Msg msg) {
        this.msg = msg;
    }
}
