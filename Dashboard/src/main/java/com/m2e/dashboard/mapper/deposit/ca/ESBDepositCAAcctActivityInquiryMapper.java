package com.m2e.dashboard.mapper.deposit.ca;

import com.m2e.dashboard.dto.deposit.ca.response.DepositTrxHistoryDTO;
import com.m2e.dashboard.jms.deposit.ca.response.account.activityinquiry.ESBDepositCAAcctActivityInquiryTrnRecDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Mapper(componentModel = "spring")
public interface ESBDepositCAAcctActivityInquiryMapper {

    ESBDepositCAAcctActivityInquiryMapper INSTANCE = Mappers.getMapper(ESBDepositCAAcctActivityInquiryMapper.class);
    static final Logger log = LoggerFactory.getLogger(ESBDepositCAAcctActivityInquiryMapper.class);

    @Mapping(source = "valueDate", target = "date")
    @Mapping(source = "valueTime", target = "processingTime")
    @Mapping(source = "serialNumber", target = "trxReference")
    @Mapping(source = "transactionDescription1", target = "trxDesc")
    @Mapping(source = "transactionDescription2", target = "trxDesc2")
    @Mapping(source = "transactionDescription3", target = "trxDesc3")
    @Mapping(source = "transactionDescription4", target = "trxDesc4")
    @Mapping(source = "transactionDescription5", target = "trxDesc5")
    @Mapping(source = "transactionDescription6", target = "trxDesc6")
    @Mapping(source = "currency", target = "currency")
    @Mapping(source = ".", target = "debit", qualifiedByName = "mapAmountToDebit")
    @Mapping(source = ".", target = "credit", qualifiedByName = "mapAmountToCredit")
    @Mapping(source = "sourceCode", target = "sourceCode")
    @Mapping(source = "tellerId", target = "tellerId")
    @Mapping(source = "branchCode", target = "branch")
    @Mapping(source = "transactionCode", target = "trxCode")
    @Mapping(source = "auxTransactionCode", target = "auxTrxCode")
    @Mapping(source = "effectiveDate", target = "expiryDate")
    @Mapping(target = "beginBalance", ignore = true) // Assuming no mapping from DTO
    @Mapping(target = "endBalance", ignore = true) // Assuming no mapping from DTO
    @Mapping(source = "virtualAccount", target = "virtualAcct")
    DepositTrxHistoryDTO mapToDepositTrxHistoryRes(ESBDepositCAAcctActivityInquiryTrnRecDTO dto);

    @Named("mapAmountToCredit")
    default String mapAmountToCredit(ESBDepositCAAcctActivityInquiryTrnRecDTO dto) {
        return "+".equalsIgnoreCase(dto.getCreditDebitIndicator()) ? dto.getAmount() : null;
    }

    @Named("mapAmountToDebit")
    default String mapAmountToDebit(ESBDepositCAAcctActivityInquiryTrnRecDTO dto) {
        return "-".equalsIgnoreCase(dto.getCreditDebitIndicator()) ? dto.getAmount() : null;
    }
}