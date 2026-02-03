package com.m2e.dashboard.mapper.af.hp;

import com.m2e.dashboard.dto.af.response.hp.AFHPTrxHistDTO;
import com.m2e.dashboard.jms.af.hp.response.account.activityinquiry.ESBAFHPAcctActivityInquiryTrnRecDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ESBAFHPAcctActivityInquiryMapper {

    ESBAFHPAcctActivityInquiryMapper INSTANCE = Mappers.getMapper(ESBAFHPAcctActivityInquiryMapper.class);

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
    @Mapping(source = "transactionDescription1", target = "trxDescription")
    @Mapping(source = "transactionDescription2", target = "trxDescription2")
    @Mapping(source = "transactionDescription3", target = "trxDescription3")
    @Mapping(source = "transactionDescription4", target = "trxDescription4")
    @Mapping(source = ".", target = "debit", qualifiedByName = "mapAmountToDebit")
    @Mapping(source = ".", target = "credit", qualifiedByName = "mapAmountToCredit")
    @Mapping(source = "effectiveDate", target = "expiryDate")
    @Mapping(source = "transactionStatus", target = "trxStatus")
    AFHPTrxHistDTO mapToHPTrxHistoryRes(ESBAFHPAcctActivityInquiryTrnRecDTO dto);

    @Named("mapAmountToCredit")
    default String mapAmountToCredit(ESBAFHPAcctActivityInquiryTrnRecDTO dto) {
        return "C".equalsIgnoreCase(dto.getTransactionType()) ? dto.getAmount() : null;
    }

    @Named("mapAmountToDebit")
    default String mapAmountToDebit(ESBAFHPAcctActivityInquiryTrnRecDTO dto) {
        return "D".equalsIgnoreCase(dto.getTransactionType()) ? dto.getAmount() : null;
    }
}