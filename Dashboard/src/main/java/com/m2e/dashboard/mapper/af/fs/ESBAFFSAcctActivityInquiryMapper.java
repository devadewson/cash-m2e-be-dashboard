package com.m2e.dashboard.mapper.af.fs;

import com.m2e.dashboard.dto.af.response.fs.AFFSTrxHistDTO;
import com.m2e.dashboard.jms.af.fs.response.account.activityinquiry.ESBAFFSAcctActivityInquiryTrnRecDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ESBAFFSAcctActivityInquiryMapper {

    ESBAFFSAcctActivityInquiryMapper INSTANCE = Mappers.getMapper(ESBAFFSAcctActivityInquiryMapper.class);

    // fields not applicable for autofinance: processingTime, virtualAcct, expiryDate
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
    AFFSTrxHistDTO mapToLoanTrxHistoryRes(ESBAFFSAcctActivityInquiryTrnRecDTO dto);

    @Named("mapAmountToCredit")
    default String mapAmountToCredit(ESBAFFSAcctActivityInquiryTrnRecDTO dto) {
        return "C".equalsIgnoreCase(dto.getTransactionType()) ? dto.getAmount() : null;
    }

    @Named("mapAmountToDebit")
    default String mapAmountToDebit(ESBAFFSAcctActivityInquiryTrnRecDTO dto) {
        return "D".equalsIgnoreCase(dto.getTransactionType()) ? dto.getAmount() : null;
    }
}