package com.m2e.dashboard.model.esbRate;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Request ESB GetExchangeRate. Isi hardcode sesuai spesifikasi, hanya MsgID yang unik. */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EsbExchangeRateRequest {

    @JsonProperty("Msg")
    private Msg msg;

    public static EsbExchangeRateRequest of(String msgId) {
        return new EsbExchangeRateRequest(new Msg(
                new MsgHeader(msgId, "01", "IDAPIXRATEINQ01", "", "S"),
                new MsgBody("", "", "KURS", "")));
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Msg {
        @JsonProperty("MsgHeader")
        private MsgHeader msgHeader;

        @JsonProperty("MsgBody")
        private MsgBody msgBody;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MsgHeader {
        @JsonProperty("MsgID")
        private String msgId;

        @JsonProperty("Ver")
        private String ver;

        @JsonProperty("SvcID")
        private String svcId;

        @JsonProperty("TxnCode")
        private String txnCode;

        @JsonProperty("Env")
        private String env;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MsgBody {
        @JsonProperty("MitraId")
        private String mitraId;

        @JsonProperty("ReffNo")
        private String reffNo;

        @JsonProperty("TranCode")
        private String tranCode;

        @JsonProperty("Currency")
        private String currency;
    }
}