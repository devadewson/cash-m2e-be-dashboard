package com.m2e.dashboard.dto.maintenance.response;

import com.m2e.dashboard.dto.common.SearchCollectionResponse;
import com.m2e.dashboard.sp.maintenance.response.MTAFSearchSpDTO;
import lombok.Data;

import java.io.Serializable;

@Data
public class MTAFSearchDTO implements Serializable {
    // mandatory attribute based on FE requirement to fix accordion problem
    private String corpCifNo;
    private SearchCollectionResponse<MTAFSearchSpDTO> datas;
}
