package com.m2e.dashboard.enums;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Enumeration of available statement frequencies.")
public enum FrequencyEnum {
    @Schema(description = "Monthly statement frequency.")
    MONTHLY,

    @Schema(description = "Yearly statement frequency.")
    YEARLY,

    @Schema(description = "Half-yearly statement frequency.")
    HALF_YEARLY,

    @Schema(description = "Quarterly statement frequency.")
    QUARTERLY,

    @Schema(description = "Custom date range for statement frequency.")
    CUSTOMISE
}
