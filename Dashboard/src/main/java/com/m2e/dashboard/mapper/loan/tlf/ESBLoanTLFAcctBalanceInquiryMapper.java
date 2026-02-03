package com.m2e.dashboard.mapper.loan.tlf;

import com.m2e.dashboard.dto.loan.tlf.response.LNTLFDTO;
import com.m2e.dashboard.enums.DateTimeFormatPatternEnum;
import com.m2e.dashboard.enums.ESBServiceType;
import com.m2e.dashboard.jms.loan.tlf.response.account.details.ESBLoanTLFAccountDTO;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Mapper(componentModel = "spring")
public abstract class ESBLoanTLFAcctBalanceInquiryMapper extends ESBLoanTLFBaseMapper {

    @Mapping(target = "acctNumber", source = "accountNumber")
    @Mapping(target = "accountCurrency", source = "currency")
    @Mapping(target = "acctName", expression = "java(mapAcctDetails(dto, \"accountName\"))")
    @Mapping(target = "drawdownAmount", expression = "java(mapAcctDetails(dto, \"amountReleased\"))")
    @Mapping(target = "outstandingBalance", expression = "java(mapAcctDetails(dto, \"ledgerBal\"))")
    @Mapping(target = "isOverdue", expression = "java(mapIsOverdue(dto, \"principalPymtDueDate\"))")
    public abstract LNTLFDTO mapToLoanTLFSummaryResp(ESBLoanTLFAccountDTO dto, @Context ESBServiceType serviceType);

    protected static boolean mapIsOverdue(ESBLoanTLFAccountDTO dto, String label) {
        String date = mapAcctDetails(dto, label);

        if (date != null && !date.equals("0")) {
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(
                    DateTimeFormatPatternEnum.yyyyMMdd.getDateFormatPattern());
            return LocalDate.parse(date, dateFormatter).isBefore(LocalDate.now());
        }
        return false;
    }
}