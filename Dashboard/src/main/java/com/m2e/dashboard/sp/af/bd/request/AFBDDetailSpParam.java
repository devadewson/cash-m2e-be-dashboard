package com.m2e.dashboard.sp.af.bd.request;

import com.m2e.dashboard.dto.common.BasePageRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Data
@AllArgsConstructor
public class AFBDDetailSpParam extends BasePageRequest {
    private String acctNumber;
    private String corpId;
    private String userId;
}