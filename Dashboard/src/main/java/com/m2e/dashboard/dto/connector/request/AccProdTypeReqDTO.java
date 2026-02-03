package com.m2e.dashboard.dto.connector.request;

import com.m2e.dashboard.dto.common.BasePageRequest;
import lombok.Data;


@Data
public class AccProdTypeReqDTO extends BasePageRequest {
    String accTypeCode;
    String providerCode;
}