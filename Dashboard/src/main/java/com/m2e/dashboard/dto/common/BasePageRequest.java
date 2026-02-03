package com.m2e.dashboard.dto.common;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
public class BasePageRequest {
    private int page;
    private int pageSize;
}
