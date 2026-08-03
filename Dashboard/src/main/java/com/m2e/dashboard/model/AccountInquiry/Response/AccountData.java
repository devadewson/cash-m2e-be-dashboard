package com.m2e.dashboard.model.AccountInquiry.Response;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

public class AccountData {
    @JsonProperty("CADataRecord")
    @JsonIgnoreProperties(ignoreUnknown = true)
    private CADataRecord cADataRecord;

    public CADataRecord getcADataRecord() {
        return cADataRecord;
    }

    public void setcADataRecord(CADataRecord cADataRecord) {
        this.cADataRecord = cADataRecord;
    }
}

