package com.m2e.dashboard.dto.base;

import lombok.Data;

import java.util.List;

@Data
public class CurrencyConversionDTO {
    private String targetCurrency;
    private List<String> convertedValues;
}
