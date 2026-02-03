package com.m2e.dashboard.jms.af.fs.request.account.details;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class ESBAFFSCertDetailsReqDTO implements Serializable {
    @JsonProperty("AccountNo")
    private String accountNo;
    @JsonProperty("CertNo")
    private String certNo;
    @JsonProperty("Currency")
    private String currency;
    @JsonProperty("AccountType")
    private String accountType;
    @JsonProperty("Region")
    private String region;
}
