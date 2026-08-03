package com.m2e.dashboard.model.BTS.Response.Sumary;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class SummaryResponse {
    @JsonProperty("responseCode")
    private String responseCode;

    @JsonProperty("responseMessage")
    private String responseMessage;

    @JsonProperty("responseData")
    private SummaryResponseData responseData;

    public String getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(String responseCode) {
        this.responseCode = responseCode;
    }

    public String getResponseMessage() {
        return responseMessage;
    }

    public void setResponseMessage(String responseMessage) {
        this.responseMessage = responseMessage;
    }

    public SummaryResponseData getResponseData() {
        return responseData;
    }

    public void setResponseData(SummaryResponseData responseData) {
        this.responseData = responseData;
    }
}
