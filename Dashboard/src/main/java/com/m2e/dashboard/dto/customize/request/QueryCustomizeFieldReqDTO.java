package com.m2e.dashboard.dto.customize.request;

import com.m2e.dashboard.enums.SubProductEnum;
import com.m2e.dashboard.enums.ScreenEnum;
import lombok.Data;

@Data
public class QueryCustomizeFieldReqDTO {
    private SubProductEnum subProduct;
    private ScreenEnum screen;
}
