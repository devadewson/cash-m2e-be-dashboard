package com.m2e.dashboard.dto.loan.plafond.request;


import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Data
@NoArgsConstructor
public class LoanPlafondReq implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Boolean realTimeIndicator;

    private String baseCurrency;
}