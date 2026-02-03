package com.m2e.dashboard.jms.deposit.fd.response.account.certdetails;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class ESBDepositFDCertsDetailDTO implements Serializable {
    @JsonProperty("Name")
    private String name;
    @JsonProperty("Value")
    private String value;
}
