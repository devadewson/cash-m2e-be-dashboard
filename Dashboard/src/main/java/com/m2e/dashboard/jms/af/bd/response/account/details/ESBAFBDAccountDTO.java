package com.m2e.dashboard.jms.af.bd.response.account.details;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class ESBAFBDAccountDTO implements Serializable {

    @JsonProperty("AccountNo")
    private String accountNumber;
    @JsonProperty("Currency")
    private String currency;
    @JsonProperty("Region")
    private String region;
    @JsonProperty("Details")
    @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
    private List<ESBAFBDAccountDetailDTO> details;
}
