package com.m2e.dashboard.jms.af.pbr.request.account.details;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
@Builder
public class ESBPBRUnitListingReqDTO implements Serializable {

    @JsonProperty("AccountNo")
    private String accountNumber;
    @JsonProperty("UnitNo")
    private String unitNumber;
    @JsonProperty("CurrencyCode")
    private String currency;
    @JsonProperty("AccountType")
    private String accountType;
    @JsonProperty("Region")
    private String region;

}
