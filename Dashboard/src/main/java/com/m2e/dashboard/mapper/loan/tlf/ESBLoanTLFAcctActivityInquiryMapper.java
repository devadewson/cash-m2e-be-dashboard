package com.m2e.dashboard.mapper.loan.tlf;

import com.m2e.dashboard.dto.loan.tlf.response.LNTLFTrxHistoryDTO;
import com.m2e.dashboard.enums.DateTimeFormatPatternEnum;
import com.m2e.dashboard.jms.loan.tlf.response.account.activityinquiry.ESBLoanTLFAcctActivityInquiryTrnRecDTO;
import com.m2e.dashboard.util.ESBValueUtil;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ESBLoanTLFAcctActivityInquiryMapper {
    ESBLoanTLFAcctActivityInquiryMapper INSTANCE = Mappers.getMapper(ESBLoanTLFAcctActivityInquiryMapper.class);

    @Mapping(target = "date", expression = "java(mapDateTime(dto.getTransactionDate(), dto.getProcessTime()))")
    @Mapping(target = "trxReference", expression = "java(dto.getTxnReference())")
    @Mapping(target = "currency", expression = "java(dto.getCurrency())")
    @Mapping(target = "trxDesc", expression = "java(dto.getTransactionDescription1())")
    @Mapping(target = "debit", expression = "java(mapCreditDebitToDebit(dto.getAmount(), dto.getTxnType()))")
    @Mapping(target = "credit", expression = "java(mapCreditDebitToCredit(dto.getAmount(), dto.getTxnType()))")
    LNTLFTrxHistoryDTO mapToLoanTrxHistoryRes(ESBLoanTLFAcctActivityInquiryTrnRecDTO dto);

	@Named("mapDateTime")
	default String mapDateTime(String dateString, String timeString) {
		
		String dateTimeStr = dateString + timeString;
		return dateString != null ? ESBValueUtil.convertDateTimeFormat(dateTimeStr,
				DateTimeFormatPatternEnum.yyyyMMddHHmmss, DateTimeFormatPatternEnum.dd_MMM_yyyy_HH_mm) : null;
	}
    
    @Named("mapCreditDebitToDebit")
    default String mapCreditDebitToDebit(String amount, String txnType) {
        return "D".equalsIgnoreCase(txnType) ? amount : "0";
    }

    @Named("mapCreditDebitToCredit")
    default String mapCreditDebitToCredit(String amount, String txnType) {
        return "C".equalsIgnoreCase(txnType) ? amount : "0";
    }
}