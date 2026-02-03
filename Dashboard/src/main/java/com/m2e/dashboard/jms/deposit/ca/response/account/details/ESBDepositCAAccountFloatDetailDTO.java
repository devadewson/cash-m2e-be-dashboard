package com.m2e.dashboard.jms.deposit.ca.response.account.details;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class ESBDepositCAAccountFloatDetailDTO implements Serializable {
    @JsonProperty("MbbLastKey")
    private String mbbLastKey;
    @JsonProperty("RecordCount")
    private String recordCount;
    @JsonProperty("AcctTrnRec")
    private List<ESBDepositCAAccountFloatDetailTrnRecDTO> acctTrnRec;
}
