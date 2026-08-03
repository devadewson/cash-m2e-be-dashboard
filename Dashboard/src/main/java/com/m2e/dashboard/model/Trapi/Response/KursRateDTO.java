package com.m2e.dashboard.model.Trapi.Response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class KursRateDTO {

    @JsonProperty("name")
    private String name;

    @JsonProperty("strIndex")
    private String strIndex;

    @JsonProperty("tenor")
    private String tenor;

    @JsonProperty("ccy")
    private String ccy;

    @JsonProperty("bidState")
    private String bidState;

    @JsonProperty("askState")
    private String askState;

    @JsonProperty("clientname")
    private String clientName;

    @JsonProperty("timeStamp")
    private String timeStamp;

    @JsonProperty("riskAmount")
    private String riskAmount;

    @JsonProperty("bid")
    private String bid;

    @JsonProperty("ask")
    private String ask;

    @JsonProperty("bidAllIn")
    private String bidAllIn;

    @JsonProperty("askAllIn")
    private String askAllIn;

    @JsonProperty("baseDPs")
    private String baseDp;

    @JsonProperty("termsDPs")
    private String termsDp;

    @JsonProperty("forwardDisplayDPs")
    private String forwardDisplayDPs;

    @JsonProperty("unitQuotation")
    private String unitQuotation;

    @JsonProperty("valueDate")
    private String valueDate;

    @JsonProperty("valueDateType")
    private String valueDateType;

    @JsonProperty("dealtAmount")
    private String dealtAmount;

    @JsonProperty("oneclickStalenessReason")
    private String oneclickStalenessReason;

    @JsonProperty("bidTrmId")
    private String bidTrmId;

    @JsonProperty("askTrmId")
    private String askTrmId;

}