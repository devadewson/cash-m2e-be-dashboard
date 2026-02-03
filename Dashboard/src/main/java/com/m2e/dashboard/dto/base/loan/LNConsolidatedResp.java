package com.m2e.dashboard.dto.base.loan;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@Builder
public class LNConsolidatedResp implements Serializable {
    private String currency;
    private String value;
}
