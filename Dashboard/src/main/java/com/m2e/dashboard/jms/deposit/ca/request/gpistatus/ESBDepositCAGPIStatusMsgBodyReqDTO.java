package com.m2e.dashboard.jms.deposit.ca.request.gpistatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.UUID;

@Data
@AllArgsConstructor
@Builder
public class ESBDepositCAGPIStatusMsgBodyReqDTO implements Serializable {

    private UUID uniqueID;
    private String transactionDateTime;
    private String serviceID;
    private String serviceType;
    private String uetr;
}
