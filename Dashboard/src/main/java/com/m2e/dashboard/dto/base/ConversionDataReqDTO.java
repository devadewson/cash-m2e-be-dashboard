package com.m2e.dashboard.dto.base;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
public class ConversionDataReqDTO {
    @Schema(description = "Amount to be converted", example = "364310638134709900.00")
    private String amount;

    @Schema(description = "Buy rate for conversion", example = "148.4")
    private String buyRate;

    @Schema(description = "Conversion unit (e.g., 100 for CHF, 1 for AUD)", example = "100")
    private String conversionUnit;
}
