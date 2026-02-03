package com.m2e.dashboard.dto.loan.tlf.request;

import com.m2e.dashboard.enums.LNProductType;
import lombok.Data;

import java.io.Serializable;

@Data
public class LNTLFTranchesDetailReqDTO implements Serializable {
    private String acctNumber;
    private String trancheNumber;
    private String currency;
    private LNProductType productType;
}