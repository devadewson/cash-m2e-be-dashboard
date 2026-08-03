package com.m2e.dashboard.model.XLBT.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AdditionalStatusCode {
    @JsonProperty("HostTxncode")
    private String hostTxncode;

    @JsonProperty("HostTxndesc")
    private String hostTxndesc;

    @JsonProperty("HostStatusCode")
    private String hostStatusCode;

    @JsonProperty("HostStatusDesc")
    private String hostStatusDesc;

    @JsonProperty("HostProductSystem")
    private String hostProductSystem;

    @JsonProperty("TXNTime")
    private String txnTime;

    @JsonProperty("MicroSvcCode")
    private String microSvcCode;

    @JsonProperty("LastInvokeDateTime")
    private String lastInvokeDateTime;

    @JsonProperty("RetryInd")
    private String retryInd;

    public String getHostTxncode() {
        return hostTxncode;
    }

    public void setHostTxncode(String hostTxncode) {
        this.hostTxncode = hostTxncode;
    }

    public String getHostTxndesc() {
        return hostTxndesc;
    }

    public void setHostTxndesc(String hostTxndesc) {
        this.hostTxndesc = hostTxndesc;
    }

    public String getHostStatusCode() {
        return hostStatusCode;
    }

    public void setHostStatusCode(String hostStatusCode) {
        this.hostStatusCode = hostStatusCode;
    }

    public String getHostStatusDesc() {
        return hostStatusDesc;
    }

    public void setHostStatusDesc(String hostStatusDesc) {
        this.hostStatusDesc = hostStatusDesc;
    }

    public String getHostProductSystem() {
        return hostProductSystem;
    }

    public void setHostProductSystem(String hostProductSystem) {
        this.hostProductSystem = hostProductSystem;
    }

    public String getTxnTime() {
        return txnTime;
    }

    public void setTxnTime(String txnTime) {
        this.txnTime = txnTime;
    }

    public String getMicroSvcCode() {
        return microSvcCode;
    }

    public void setMicroSvcCode(String microSvcCode) {
        this.microSvcCode = microSvcCode;
    }

    public String getLastInvokeDateTime() {
        return lastInvokeDateTime;
    }

    public void setLastInvokeDateTime(String lastInvokeDateTime) {
        this.lastInvokeDateTime = lastInvokeDateTime;
    }

    public String getRetryInd() {
        return retryInd;
    }

    public void setRetryInd(String retryInd) {
        this.retryInd = retryInd;
    }
}
