package com.m2e.dashboard.mapper.loan.tlf;

import com.m2e.dashboard.dto.loan.tlf.response.LNTLFTrxHistoryDTO;
import com.m2e.dashboard.jms.loan.tlf.response.account.activityinquiry.ESBLoanTLFAcctActivityInquiryTrnRecDTO;
import com.m2e.dashboard.util.ESBValueUtil;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ESBLoanTLFSGAcctActivityInquiryMapper {
    ESBLoanTLFAcctActivityInquiryMapper INSTANCE = Mappers.getMapper(ESBLoanTLFAcctActivityInquiryMapper.class);

    @Mapping(target = "date", expression = "java(mapDateSG(dto.getTransactionDate()))")
    @Mapping(target = "trxReference", expression = "java(dto.getTxnReference())")
    @Mapping(target = "currency", expression = "java(dto.getCurrency())")
    @Mapping(target = "trxDesc", expression = "java(dto.getTransactionDescription1())")
    @Mapping(target = "debit", expression = "java(mapAmountToDebit(dto.getAmount(), dto.getTxnType()))")
    @Mapping(target = "credit", expression = "java(mapAmountToCredit(dto.getAmount(), dto.getTxnType()))")
    LNTLFTrxHistoryDTO mapToLoanTrxHistoryRes(ESBLoanTLFAcctActivityInquiryTrnRecDTO dto);

    @Named("mapDateSG")
    default String mapDateSG(String dateString) {
        return dateString != null ? ESBValueUtil.convertDateFormat(dateString) : null;
    }

    @Named("mapAmountToDebit")
    default String mapAmountToDebit(String amount, String txnType) {
        return "-".equalsIgnoreCase(txnType) ? ESBValueUtil.formatSgEsbAmountValue(amount) : null;
    }

    @Named("mapAmountToCredit")
    default String mapAmountToCredit(String amount, String txnType) {
        return "+".equalsIgnoreCase(txnType) ? ESBValueUtil.formatSgEsbAmountValue(amount) : null;
    }
}