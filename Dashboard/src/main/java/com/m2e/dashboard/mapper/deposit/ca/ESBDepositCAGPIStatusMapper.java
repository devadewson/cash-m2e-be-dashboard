package com.m2e.dashboard.mapper.deposit.ca;

import com.m2e.dashboard.dto.deposit.ca.response.DepositGPIStatusDTO;
import com.m2e.dashboard.jms.deposit.ca.response.gpistatus.ESBDepositCAGPIStatusPaymentTrxnDetailsDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface ESBDepositCAGPIStatusMapper {

    @Mappings({
            @Mapping(source = "uetr", target = "uetrNumber"),
            @Mapping(source = "transactionStatus.status", target = "gpiStatus")
    })
    DepositGPIStatusDTO mapPaymentTransactionDetailResponseToDepositGPIStatusRes(ESBDepositCAGPIStatusPaymentTrxnDetailsDTO host);
}