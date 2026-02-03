package com.m2e.dashboard.jms.af.fs.response.account.details;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class ESBAFFSCertDetailsDTO implements Serializable {
    @JsonProperty("AccountNo")
    private String accountNumber;
    @JsonProperty("CertNo")
    private String certNumber;
    @JsonProperty("Currency")
    private String currency;
    @JsonProperty("Region")
    private String region;
    @JsonProperty("Details")
    private List<ESBAFFSAccountDTO> details;
}
