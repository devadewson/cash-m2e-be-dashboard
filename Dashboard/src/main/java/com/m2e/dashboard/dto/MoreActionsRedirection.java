package com.m2e.dashboard.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MoreActionsRedirection implements Serializable {
    @JsonProperty("isEntitled")
    private boolean isEntitled;
    private String redirectModule;
    private String applicationCode;
    private String moduleNo;
}