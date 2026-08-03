package com.m2e.dashboard.model.XLBT.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MsgHeader {
    @JsonProperty("MsgID")
    private String msgID;
    @JsonProperty("Ver")
    private String ver;
    @JsonProperty("SvcID")
    private String svcID;
    @JsonProperty("TxnCode")
    private String txnCode;
    @JsonProperty("Env")
    private String env;
    @JsonProperty("HostID")
    private String hostID;

    public String getMsgID() {
        return msgID;
    }

    public void setMsgID(String msgID) {
        this.msgID = msgID;
    }

    public String getVer() {
        return ver;
    }

    public void setVer(String ver) {
        this.ver = ver;
    }

    public String getSvcID() {
        return svcID;
    }

    public void setSvcID(String svcID) {
        this.svcID = svcID;
    }

    public String getTxnCode() {
        return txnCode;
    }

    public void setTxnCode(String txnCode) {
        this.txnCode = txnCode;
    }

    public String getEnv() {
        return env;
    }

    public void setEnv(String env) {
        this.env = env;
    }

    public String getHostID() {
        return hostID;
    }

    public void setHostID(String hostID) {
        this.hostID = hostID;
    }
}
