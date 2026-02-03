package com.m2e.dashboard.dto.base;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
public class CurrencyConversionReqDTO {
    @Schema(description = "Target currency code", example = "CHF")
    private String targetCurrency;
    @Schema(description = "List of amounts with buy rates and conversion units")
    private List<ConversionDataReqDTO> data;
}
