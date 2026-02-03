package com.m2e.dashboard.jms.deposit.ca.response.gpistatus;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class ESBDepositCAGPIStatusAgentDTO implements Serializable {
    @JsonProperty("BICFI")
    private String bicfi;

    @JsonProperty("Name")
    private String name;
}
