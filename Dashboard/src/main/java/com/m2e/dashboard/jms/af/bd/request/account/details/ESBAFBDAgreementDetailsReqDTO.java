package com.m2e.dashboard.jms.af.bd.request.account.details;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class ESBAFBDAgreementDetailsReqDTO implements Serializable {
    @JsonProperty("AccountNo")
    private String accountNumber;
    @JsonProperty("BlockNo")
    private String blockNumber;
    @JsonProperty("CurrencyCode")
    private String currency;
    @JsonProperty("AccountType")
    private String accountType;
    @JsonProperty("AgreementNo")
    private String agreementNumber;
    @JsonProperty("Region")
    private String region;
}
