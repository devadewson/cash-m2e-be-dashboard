package com.m2e.dashboard.dto.loan.tlf;


import com.m2e.dashboard.dto.common.SearchCollectionResponse;
import com.m2e.dashboard.dto.loan.tlf.LoanPbDrawDTO;
import lombok.Data;

import java.io.Serializable;

@Data
public class LoanPbFacilityDTO implements Serializable {
    private String noteNo;
    private String currency;
    private String originalLimit;
    private String availableLimit;
    private String availablePeriod;
    private SearchCollectionResponse<LoanPbDrawDTO> draws;
}
