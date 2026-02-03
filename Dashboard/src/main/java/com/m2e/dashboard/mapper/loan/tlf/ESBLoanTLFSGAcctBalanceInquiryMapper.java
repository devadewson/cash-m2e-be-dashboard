package com.m2e.dashboard.mapper.loan.tlf;

import com.m2e.dashboard.dto.loan.tlf.response.LNTLFDTO;
import com.m2e.dashboard.jms.loan.tlf.response.account.details.ESBLoanTLFAccountSGDTO;
import com.m2e.dashboard.jms.loan.tlf.response.account.details.ESBLoanTLFDetailDTO;
import com.m2e.dashboard.util.ESBValueUtil;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.List;

import static com.m2e.dashboard.util.UtilityFunctions.convertSpBooleanResult;

@Mapper(componentModel = "spring")
public interface ESBLoanTLFSGAcctBalanceInquiryMapper {

    @Mapping(target = "acctNumber", source = "accountNumber", qualifiedByName = "mapAccountNumber")
    @Mapping(target = "accountCurrency", source = "currency")
    @Mapping(target = "drawdownAmount", source = "details", qualifiedByName = "mapDrawdownAmount")
    @Mapping(target = "outstandingBalance", source = "details", qualifiedByName = "mapOutstandingBalance")
    @Mapping(target = "isOverdue", source = "details", qualifiedByName = "mapIsOverdue")
    LNTLFDTO mapToLoanTLFSummaryResp(ESBLoanTLFAccountSGDTO dto);

    @Named("mapAccountNumber")
    default String mapAccountNumber(String esbAccountNumber) {
        if (esbAccountNumber == null || esbAccountNumber.isBlank() || !esbAccountNumber.matches("\\d+")) return null;
        return new BigInteger(esbAccountNumber).toString();
    }

    @Named("mapDrawdownAmount")
    default String mapDrawdownAmount(List<ESBLoanTLFDetailDTO> details) {
        return details.stream()
                .filter(detail -> "releaseAmt".equalsIgnoreCase(detail.getName()))
                .map(detail -> ESBValueUtil.formatSgEsbAmountValue(detail.getValue()))
                .findFirst()
                .orElse(null);
    }

    @Named("mapOutstandingBalance")
    default String mapOutstandingBalance(List<ESBLoanTLFDetailDTO> details) {
        if (isTlast(details)) return null;

        return details.stream()
                .filter(detail -> "outstandingBal".equalsIgnoreCase(detail.getName()))
                .map(detail -> ESBValueUtil.formatSgEsbAmountValue(detail.getValue()))
                .findFirst()
                .orElse(null);
    }

    @Named("mapIsOverdue")
    default boolean mapIsOverdue(List<ESBLoanTLFDetailDTO> details) {
        String date = details.stream()
                .filter(detail -> "nextPymtDueDate".equalsIgnoreCase(detail.getName()))
                .map(ESBLoanTLFDetailDTO::getValue)
                .findFirst()
                .orElse(null);

        LocalDate convertedDate = ESBValueUtil.convertDateFromStringToLocalDate(date);
        return convertedDate != null && convertedDate.isBefore(LocalDate.now());
    }

    default boolean isTlast(List<ESBLoanTLFDetailDTO> details) {
        return details.stream()
                .filter(detail -> "tlastInd".equalsIgnoreCase(detail.getName()))
                .map(detail -> convertSpBooleanResult(detail.getValue()))
                .findFirst()
                .orElse(false);
    }
}