package com.m2e.dashboard.dto.maintenance.response;

import com.m2e.dashboard.dto.common.SearchCollectionResponse;
import com.m2e.dashboard.sp.maintenance.response.MTDepositSearchSpDTO;
import lombok.Data;

import java.io.Serializable;

@Data
public class MTDepositSearchDTO implements Serializable {
    // mandatory attribute based on FE requirement to fix accordion problem
    private String corpCifNo;
    private SearchCollectionResponse<MTDepositSearchSpDTO> datas;
}
