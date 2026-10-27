package com.m2e.dashboard.dto.loan.plafond.response;


import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Data
@NoArgsConstructor
public class LoanPlafondStatsDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String plafond;

    private String lastUpdatedDate;
    private String lastUpdatedDateTime;
}
