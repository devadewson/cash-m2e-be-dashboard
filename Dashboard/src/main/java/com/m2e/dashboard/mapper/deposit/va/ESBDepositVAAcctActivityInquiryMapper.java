package com.m2e.dashboard.mapper.deposit.va;

import com.m2e.dashboard.dto.deposit.va.response.DepositVATransHistSearchDTO;
import com.m2e.dashboard.jms.deposit.ca.response.account.activityinquiry.ESBDepositCAAcctActivityInquiryTrnRecDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Mapper(componentModel = "spring")
public interface ESBDepositVAAcctActivityInquiryMapper {

    ESBDepositVAAcctActivityInquiryMapper INSTANCE = Mappers.getMapper(ESBDepositVAAcctActivityInquiryMapper.class);
    static final Logger log = LoggerFactory.getLogger(ESBDepositVAAcctActivityInquiryMapper.class);

    @Mapping(source = "valueDate", target = "date")
    @Mapping(source = "virtualAccount", target = "virtualAcctNumber")
    @Mapping(source = "serialNumber", target = "virtualAcctRefNumber")
    @Mapping(source = "transactionDescription1", target = "trxDescription")
    @Mapping(source = ".", target = "debit", qualifiedByName = "mapAmountToDebit")
    @Mapping(source = ".", target = "credit", qualifiedByName = "mapAmountToCredit")
    @Mapping(source = "valueTime", target = "processingTime")
    @Mapping(source = "transactionCode", target = "trxCode")
    @Mapping(source = "effectiveDate", target = "postingDate")
    @Mapping(source = "sourceCode", target = "sourceCode")
    @Mapping(source = "valueTime", target = "trxTime")
    @Mapping(source = "tellerId", target = "tellerId")
    @Mapping(source = "currency", target = "currency")
    @Mapping(source = "valueDate", target = "transactionDate")
    DepositVATransHistSearchDTO mapToDepositTrxHistoryRes(ESBDepositCAAcctActivityInquiryTrnRecDTO dto);

    @Named("mapAmountToCredit")
    default String mapAmountToCredit(ESBDepositCAAcctActivityInquiryTrnRecDTO dto) {
        return "+".equalsIgnoreCase(dto.getCreditDebitIndicator()) ? dto.getAmount() : null;
    }

    @Named("mapAmountToDebit")
    default String mapAmountToDebit(ESBDepositCAAcctActivityInquiryTrnRecDTO dto) {
        return "-".equalsIgnoreCase(dto.getCreditDebitIndicator()) ? dto.getAmount() : null;
    }
}