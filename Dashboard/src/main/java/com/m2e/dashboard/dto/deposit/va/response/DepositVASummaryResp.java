package com.m2e.dashboard.dto.deposit.va.response;

import com.m2e.dashboard.dto.common.SearchCollectionResponse;
import lombok.Data;

import java.io.Serializable;

@Data
public class DepositVASummaryResp implements Serializable {
    private String corpCifNo;     // mandatory attribute based on FE requirement to fix accordion problem
    private SearchCollectionResponse<DepositVASubSearchDTO> datas;
}
