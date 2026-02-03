package com.m2e.dashboard.jms.deposit.fd.response.account.certdetails;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.m2e.dashboard.jms.common.response.ESBDetailDTO;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class ESBDepositFDCertsDTO implements Serializable {
    @JsonProperty("MbbLastKey")
    private String mbbLastKey;
    @JsonProperty("AccountNo")
    private String accountNumber;
    @JsonProperty("Currency")
    private String currency;
    @JsonProperty("Details")
    private List<List<ESBDetailDTO>> details;
}