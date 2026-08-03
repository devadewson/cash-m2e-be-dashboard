package com.m2e.dashboard.model.XLBT.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MsgWraper {
    public MsgWraper( ) {
        this.msg = new com.m2e.dashboard.model.XLBT.response.Msg();
    }

    @JsonProperty("Msg")
    private com.m2e.dashboard.model.XLBT.response.Msg msg;


    public Msg getMsg() {
        return msg;
    }

    public void setMsg(Msg msg) {
        this.msg = msg;
    }
}

