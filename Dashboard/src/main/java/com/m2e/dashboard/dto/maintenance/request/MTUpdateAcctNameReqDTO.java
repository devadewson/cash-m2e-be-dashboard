package com.m2e.dashboard.dto.maintenance.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MTUpdateAcctNameReqDTO implements Serializable {
    private String acctNumber;
    private String newAcctName;
}
