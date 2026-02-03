package com.m2e.dashboard.jms.af.common;

import com.m2e.dashboard.jms.common.response.ESBMetadataDTO;
import com.m2e.dashboard.service.BaseESBMetadata;
import lombok.Data;

import java.util.List;

@Data
public class AFAcctListESBDTO implements BaseESBMetadata {
    private List<?> acctListing;
    private ESBMetadataDTO esbMetadata;
}
