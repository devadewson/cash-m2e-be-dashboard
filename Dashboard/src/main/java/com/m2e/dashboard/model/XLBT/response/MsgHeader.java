package com.m2e.dashboard.model.XLBT.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

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

    @JsonProperty("StatusCode")
    private String statusCode;

    @JsonProperty("StatusDesc")
    private String statusDesc;

    @JsonProperty("E2ETime")
    private String e2ETime;

    @JsonProperty("AdditionalStatusCodes")
    private List<AdditionalStatusCode> additionalStatusCodes;

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

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public String getStatusDesc() {
        return statusDesc;
    }

    public void setStatusDesc(String statusDesc) {
        this.statusDesc = statusDesc;
    }

    public String getE2ETime() {
        return e2ETime;
    }

    public void setE2ETime(String e2ETime) {
        this.e2ETime = e2ETime;
    }

    public List<AdditionalStatusCode> getAdditionalStatusCodes() {
        return additionalStatusCodes;
    }

    public void setAdditionalStatusCodes(List<AdditionalStatusCode> additionalStatusCodes) {
        this.additionalStatusCodes = additionalStatusCodes;
    }
}
