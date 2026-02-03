package com.m2e.dashboard.dto.af.response.fs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AFFSAcctStatsDTO {
    private boolean displayShowViewBy = false;
    private String lastUpdatedDateTime;
    private String lastUpdatedDate;
}
