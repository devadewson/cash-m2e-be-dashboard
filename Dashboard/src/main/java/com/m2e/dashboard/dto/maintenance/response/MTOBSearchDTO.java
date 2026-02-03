package com.m2e.dashboard.dto.maintenance.response;

import com.m2e.dashboard.dto.common.SearchCollectionResponse;
import com.m2e.dashboard.sp.maintenance.response.MTOBSearchSpDTO;
import lombok.Data;

import java.io.Serializable;

@Data
public class MTOBSearchDTO implements Serializable {
    private SearchCollectionResponse<MTOBSearchSpDTO> datas;
}
