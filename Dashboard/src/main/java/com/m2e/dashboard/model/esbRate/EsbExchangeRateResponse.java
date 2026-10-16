package com.m2e.dashboard.model.esbRate;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class EsbExchangeRateResponse {

    @JsonProperty("Msg")
    private Msg msg;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Msg {
        @JsonProperty("MsgHeader")
        private MsgHeader msgHeader;

        @JsonProperty("MsgBody")
        private MsgBody msgBody;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class MsgHeader {
        @JsonProperty("MsgID")
        private String msgId;

        @JsonProperty("StatusCode")
        private String statusCode;

        @JsonProperty("StatusDesc")
        private String statusDesc;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class MsgBody {
        @JsonProperty("ExchangeRateData")
        private ExchangeRateData exchangeRateData;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ExchangeRateData {
        @JsonProperty("Description")
        private String description;

        @JsonProperty("TimeStamp")
        private String timeStamp;

        @JsonProperty("ExchangeRateElement")
        private List<ExchangeRateElement> exchangeRateElement;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ExchangeRateElement {
        @JsonProperty("Currency")
        private String currency;

        @JsonProperty("BNBuyRate")
        private String bnBuyRate;

        @JsonProperty("BNSellRate")
        private String bnSellRate;

        @JsonProperty("TTBuyRate")
        private String ttBuyRate;

        @JsonProperty("TTSellRate")
        private String ttSellRate;
    }
}
