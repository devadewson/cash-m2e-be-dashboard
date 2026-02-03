package com.m2e.dashboard.service;

import com.m2e.dashboard.jms.common.response.ESBMetadataDTO;

public interface BaseESBMetadata {
    void setEsbMetadata(ESBMetadataDTO metadata);
    ESBMetadataDTO getEsbMetadata();
}
