package com.m2e.dashboard.model.BTS.Response.List;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ListResponse {

        @JsonProperty("responseCode")
        private String responseCode;

        @JsonProperty("responseMessage")
        private String responseMessage;

        @JsonProperty("responseData")
        private ListResponseData responseData;

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

    public ListResponseData getResponseData() {
        return responseData;
    }

    public void setResponseData(ListResponseData responseData) {
        this.responseData = responseData;
    }
}

