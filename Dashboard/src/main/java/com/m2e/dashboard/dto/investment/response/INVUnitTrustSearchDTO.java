package com.m2e.dashboard.dto.investment.response;

import com.m2e.dashboard.dto.common.SearchCollectionResponse;
import com.m2e.dashboard.sp.investment.response.INVUnitTrustSearchSpDTO;
import lombok.Data;

import java.io.Serializable;


@Data
public class INVUnitTrustSearchDTO implements Serializable {
    // mandatory attribute based on FE requirement to fix accordion problem
    private String corpCifNo;
    private SearchCollectionResponse<INVUnitTrustSearchSpDTO> datas;
}
