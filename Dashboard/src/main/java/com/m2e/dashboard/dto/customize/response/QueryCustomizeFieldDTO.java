package com.m2e.dashboard.dto.customize.response;

import lombok.Data;

import java.util.Set;

@Data
public class QueryCustomizeFieldDTO {
    private Set<String> selectedField;
}
