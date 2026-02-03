package com.m2e.dashboard.jms.af.bd.response.account.details;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class ESBAFBDBlockDetailDTO implements Serializable {

    @JsonProperty("AccountNo")
    private String accountNumber;
    @JsonProperty("BlockNo")
    private String blockNumber;
    @JsonProperty("Currency")
    private String currency;
    @JsonProperty("Region")
    private String region;
    @JsonProperty("Details")
    private List<ESBAFBDAccountDetailDTO> details;
}
