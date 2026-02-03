package com.m2e.dashboard.jms.deposit.fd.response.account.balanceinquiry;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class ESBDepositFDAccountDetailDTO implements Serializable {
    @JsonProperty("Name")
    private String name;
    @JsonProperty("Value")
    private String value;
}
