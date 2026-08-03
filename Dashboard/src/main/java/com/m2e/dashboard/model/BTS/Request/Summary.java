package com.m2e.dashboard.model.BTS.Request;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Summary {
    @JsonProperty("userId")
    private String userId;
    @JsonProperty("cif")
    private String cif;
    @JsonProperty("custNo")
    private String custNo;
    @JsonProperty("locale")
    private String locale;
    @JsonProperty("uniqueId")
    private String uniqueId;
    @JsonProperty("transactionDate")
    private String transactionDate;
    @JsonProperty("indicator")
    private String indicator;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCif() {
        return cif;
    }

    public void setCif(String cif) {
        this.cif = cif;
    }

    public String getCustNo() {
        return custNo;
    }

    public void setCustNo(String custNo) {
        this.custNo = custNo;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    public String getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(String transactionDate) {
        this.transactionDate = transactionDate;
    }

    public String getIndicator() {
        return indicator;
    }

    public void setIndicator(String indicator) {
        this.indicator = indicator;
    }
}
