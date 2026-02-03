package com.m2e.dashboard.dto.einvoice.response;

import com.m2e.dashboard.jms.common.response.ESBMetadataDTO;
import com.m2e.dashboard.service.BaseESBMetadata;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class EInvoiceValidateTINDTO implements BaseESBMetadata {
    private String responseStatus;
    private ESBMetadataDTO esbMetadata;
}
