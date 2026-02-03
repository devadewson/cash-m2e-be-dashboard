package com.m2e.dashboard.mapper.af.pbr;

import com.m2e.dashboard.dto.af.response.pbr.AFPBRTrxHistoryDTO;
import com.m2e.dashboard.jms.af.hp.response.account.activityinquiry.ESBAFHPAcctActivityInquiryTrnRecDTO;
import com.m2e.dashboard.jms.af.pbr.response.account.activityinquiry.ESBAFPBRAcctActivityInquiryTrnRecDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ESBAFPBRAcctActivityInquiryMapper {

    ESBAFPBRAcctActivityInquiryMapper INSTANCE = Mappers.getMapper(ESBAFPBRAcctActivityInquiryMapper.class);

    @Mapping(source = "currency", target = "currency")
    @Mapping(source = "valueDate", target = "trxDate")
    @Mapping(source = "valueTime", target = "processingTime")
    @Mapping(source = "transactionCode", target = "trxCode")
    @Mapping(source = "transactionType", target = "trxType")
    @Mapping(source = "auxTransactionCode", target = "auxiliaryTrxCode")
    @Mapping(source = "sourceCode", target = "srcCode")
    @Mapping(source = "tellerId", target = "tellerId")
    @Mapping(source = "ownerInstitution", target = "ownerInstitution")
    @Mapping(source = "reference", target = "trxReference")
    @Mapping(source = "transactionDescription1", target = "trxDesc")
    @Mapping(source = "transactionDescription2", target = "trxDesc2")
    @Mapping(source = "transactionDescription3", target = "trxDesc3")
    @Mapping(source = "transactionDescription4", target = "trxDesc4")
    @Mapping(source = ".", target = "debit", qualifiedByName = "mapAmountToDebit")
    @Mapping(source = ".", target = "credit", qualifiedByName = "mapAmountToCredit")
    @Mapping(source = "effectiveDate", target = "expiryDate")
    @Mapping(source = "transactionStatus", target = "trxStatus")
    AFPBRTrxHistoryDTO mapToTrxHistoryRes(ESBAFPBRAcctActivityInquiryTrnRecDTO dto);

    @Named("mapAmountToCredit")
    default String mapAmountToCredit(ESBAFPBRAcctActivityInquiryTrnRecDTO dto) {
        return "C".equalsIgnoreCase(dto.getTransactionType()) ? dto.getAmount() : null;
    }

    @Named("mapAmountToDebit")
    default String mapAmountToDebit(ESBAFPBRAcctActivityInquiryTrnRecDTO dto) {
        return "D".equalsIgnoreCase(dto.getTransactionType()) ? dto.getAmount() : null;
    }
}