package com.m2e.dashboard.model.AccountInquiry.Response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

public class MsgBody {
    @JsonProperty("responseDetail")
    private Object responseDetail;

    @JsonProperty("AccountInformationResponseData")
    @JsonIgnoreProperties(ignoreUnknown = true)
    private AccountInformationResponseData accountInformationResponseData;

    public Object getResponseDetail() {
        return responseDetail;
    }

    public void setResponseDetail(Object responseDetail) {
        this.responseDetail = responseDetail;
    }

    public AccountInformationResponseData getAccountInformationResponseData() {
        return accountInformationResponseData;
    }

    public void setAccountInformationResponseData(AccountInformationResponseData accountInformationResponseData) {
        this.accountInformationResponseData = accountInformationResponseData;
    }
}
