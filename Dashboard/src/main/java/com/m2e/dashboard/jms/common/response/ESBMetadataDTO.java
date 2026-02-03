package com.m2e.dashboard.jms.common.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ESBMetadataDTO implements Serializable {
    private Boolean isESBError = Boolean.FALSE;
    private String mtStatusCode;
    private String mtStatusDesc;
    private String esbStatusCode;
    private String esbStatusDesc;
    private String hostStatusCode;
    private String hostStatusDesc;
}
