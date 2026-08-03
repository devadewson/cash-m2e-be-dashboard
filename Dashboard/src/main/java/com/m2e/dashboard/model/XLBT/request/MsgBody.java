package com.m2e.dashboard.model.XLBT.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MsgBody {
    @JsonProperty("CIFNo")
    private String cifNo;

    public String getCifNo() {
        return cifNo;
    }

    public void setCifNo(String cifNo) {
        this.cifNo = cifNo;
    }
}
