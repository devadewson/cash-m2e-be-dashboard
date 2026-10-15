package com.m2e.dashboard.dto.loan.tlf;


import lombok.Data;

import java.io.Serializable;

@Data
public class LoanPbAcctStatsDTO implements Serializable {
    private Integer numberOfFacilities;
    private String consolidatedInitialLimit;
    private String consolidatedOutstandingBalance;
    private String currency;
    private String lastUpdatedDate;
    private String lastUpdatedDateTime;
}