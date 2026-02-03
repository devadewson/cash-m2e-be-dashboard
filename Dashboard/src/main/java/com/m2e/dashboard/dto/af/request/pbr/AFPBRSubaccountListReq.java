package com.m2e.dashboard.dto.af.request.pbr;

import com.m2e.dashboard.dto.common.BasePageRequest;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
public class AFPBRSubaccountListReq extends BasePageRequest {
    private String masterAcctNumber;
    private String searchParam;
}
