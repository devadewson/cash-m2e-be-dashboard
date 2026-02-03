package com.m2e.dashboard.jms.deposit.ca.response.gpistatus;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class ESBDepositCAGPIStatusForeignExchangeDetailDTO implements Serializable {
    @JsonProperty("source_currency")
    private String sourceCurrency;

    @JsonProperty("target_currency")
    private String targetCurrency;

    @JsonProperty("exchange_rate")
    private String exchangeRate;
}
