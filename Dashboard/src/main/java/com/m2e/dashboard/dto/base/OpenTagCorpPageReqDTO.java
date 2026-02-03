package com.m2e.dashboard.dto.base;

import com.m2e.dashboard.dto.common.BasePageRequest;
import com.m2e.dashboard.enums.SourcePageEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class OpenTagCorpPageReqDTO extends BasePageRequest {
    private String searchParam;
    private String product;
    private String subProduct;
    private SourcePageEnum source;
}
