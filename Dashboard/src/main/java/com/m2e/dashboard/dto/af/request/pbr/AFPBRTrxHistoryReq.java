package com.m2e.dashboard.dto.af.request.pbr;

import com.m2e.dashboard.dto.af.request.AFTrxHistoryFilterDTO;
import com.m2e.dashboard.dto.common.BasePageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@Data
public class AFPBRTrxHistoryReq extends BasePageRequest implements Serializable {
    private String acctNumber;
    private String searchParam;
    private AFTrxHistoryFilterDTO filters;
    private String currency;
    private String mbbLastKey;
}