package com.m2e.dashboard.sp.maintenance.response;

import lombok.Data;

@Data
public class MTResetAcctNameSpDTO {
    private Integer rowsUpdated;

    public MTResetAcctNameSpDTO() {
        this.rowsUpdated = 0;
    }
}