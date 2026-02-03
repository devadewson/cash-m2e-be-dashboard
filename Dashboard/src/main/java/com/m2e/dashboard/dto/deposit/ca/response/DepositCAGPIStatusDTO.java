package com.m2e.dashboard.dto.deposit.ca.response;

import com.m2e.dashboard.jms.common.response.ESBMetadataDTO;
import com.m2e.dashboard.service.BaseESBMetadata;
import lombok.Data;

import java.util.List;

@Data
public class DepositCAGPIStatusDTO implements BaseESBMetadata {
    private String uetrNumber;
    private String gpiStatus;
    private String gpiReason;
    private String totalElapsedTime;
    private List<DepositCAGPIBankDTO> bankList;
    private ESBMetadataDTO esbMetadata;
}
