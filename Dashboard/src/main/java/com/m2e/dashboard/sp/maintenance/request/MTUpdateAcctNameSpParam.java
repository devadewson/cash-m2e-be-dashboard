package com.m2e.dashboard.sp.maintenance.request;

import lombok.Data;

import java.sql.Timestamp;
import java.time.OffsetDateTime;

@Data
public class MTUpdateAcctNameSpParam {
    private String acctNumber;
    private String acctPrefName;
    private String updatedBy;
    private OffsetDateTime dtUpdated;
    private Timestamp tsUpdated;
}
