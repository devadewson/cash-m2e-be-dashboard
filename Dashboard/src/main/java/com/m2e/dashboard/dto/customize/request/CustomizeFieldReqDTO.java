package com.m2e.dashboard.dto.customize.request;

import com.m2e.dashboard.enums.SubProductEnum;
import com.m2e.dashboard.enums.ScreenEnum;
import lombok.Data;

import java.util.Set;

@Data
public class CustomizeFieldReqDTO {
    private SubProductEnum subProduct;
    private ScreenEnum screen;
    private Set<String> selectedField;
}
