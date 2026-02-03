package com.m2e.dashboard.dto.af.response.bd;

import lombok.Data;

@Data
public class AFBDAcctStatsDTO {
    private String lastUpdatedDateTime;
    private String lastUpdatedDate;
    private String outstandingBalance;
    private boolean displayShowViewBy = true;
    private String provider;
}
