package com.m2e.dashboard.mapper.af.bd;

import com.m2e.dashboard.dto.af.response.bd.*;
import com.m2e.dashboard.jms.af.bd.response.account.details.*;
import com.m2e.dashboard.util.ESBValueUtil;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.Objects;

@Mapper(componentModel = "spring")
public interface ESBAFBDAcctListInquiryMapper {

    ESBAFBDAcctListInquiryMapper INSTANCE = Mappers.getMapper(ESBAFBDAcctListInquiryMapper.class);

    @Mapping(source = "blockNumber", target = "acctNumber")
    @Mapping(target = "outstandingBalance", expression = "java(mapAcctDetails(dto.getDetails(), \"oustandingBalance\"))")
    @Mapping(target = "financedAmt", expression = "java(mapAcctDetails(dto.getDetails(), \"financedAmount\"))")
    @Mapping(target = "amountDue", expression = "java(mapAcctDetails(dto.getDetails(), \"amountInArrears\"))")
    @Mapping(target = "settlementAmount", expression = "java(mapAcctDetails(dto.getDetails(), \"settlementAmount\"))")
    @Mapping(source = "region", target = "provider")
    AFBDAcctListViewAcctDTO mapToSummaryResp(ESBAFBDBlockDetailDTO dto);

    @Mapping(source = "accountNumber", target = "masterAcctNumber")
    @Mapping(target = "totalOutstandingBalance", expression = "java(mapAcctDetails(dto.getDetails(), \"OustandingBalance\"))")
    @Mapping(target = "totalFacilityLimit", expression = "java(mapAcctDetails(dto.getDetails(), \"facilityLimit\"))")
    @Mapping(source = "region", target = "provider")
    AFBDAcctListViewFacilityDTO mapToMasterSummaryResp(ESBAFBDAccountDTO dto);

    @Mapping(source = "blockNumber", target = "acctNumber")
    @Mapping(target = "financedAmt", expression = "java(mapAcctDetails(dto.getDetails(), \"financedAmount\"))")
    @Mapping(target = "outstandingBalance", expression = "java(mapAcctDetails(dto.getDetails(), \"oustandingBalance\"))")
    @Mapping(target = "amountDue", expression = "java(mapAcctDetails(dto.getDetails(), \"amountInArrears\"))")
    AFBDSubMasterAcctListDTO mapToMasterSubSummaryResp(ESBAFBDBlockDetailDTO dto);

    @Mapping(source = "agreementNumber", target = "agreementNumber")
    @Mapping(target = "financedAmt", expression = "java(mapAcctDetails(dto.getDetails(), \"financedAmount\"))")
    @Mapping(target = "outstandingBalance", expression = "java(mapAcctDetails(dto.getDetails(), \"oustandingBalance\"))")
    @Mapping(target = "installmentAmount", expression = "java(mapAcctDetails(dto.getDetails(), \"installmentAmount\"))")
    @Mapping(target = "amountDue", expression = "java(mapAcctDetails(dto.getDetails(), \"amountInArrears\"))")
    @Mapping(target = "settlementAmount", expression = "java(mapAcctDetails(dto.getDetails(), \"settlementAmount\"))")
    AFBDSubChildAcctListDTO mapToAcctSubSummaryResp(ESBAFBDAgreementDetailDTO dto);

    @Mapping(target = "acctNumber", expression = "java(mapDebitNote(dto.getDetails(), \"blockNo\"))")
    @Mapping(target = "blockAmt", expression = "java(mapDebitNote(dto.getDetails(), \"blockAmount\"))")
    @Mapping(target = "disbursementDate", expression = "java(mapDebitNoteDate(dto.getDetails(), \"dateReimbursed\"))")
    AFBDDebitNoteListDTO mapToDebitNoteSummaryResp(ESBAFBDDebitNoteDetailDTO dto);

    @Mapping(target = "blockNumber", expression = "java(mapSGAcctNumber(dto.getAccountNumber()))")
    @Mapping(target = "outstandingBalance", expression = "java(mapAcctDetailsSG(dto.getDetails(), \"oustandingBalance\"))")
    @Mapping(target = "financedAmt", expression = "java(mapAcctDetailsSG(dto.getDetails(), \"financedAmount\"))")
    @Mapping(target = "instalmentAmt", expression = "java(mapAcctDetailsSG(dto.getDetails(), \"installmentAmount\"))")
    @Mapping(target = "settlementAmount", expression = "java(mapAcctDetailsSG(dto.getDetails(), \"settlementAmount\"))")
    @Mapping(target = "instalmentAmtOverdue", expression = "java(mapAcctDetailsSG(dto.getDetails(), \"overdueInstalmentAmount\"))")
    @Mapping(source = "region", target = "provider")
    @Mapping(target = "collateralId", expression = "java(mapAcctDetails(dto.getDetails(), \"collateralID\"))")
    @Mapping(target = "tlastIndicator", expression = "java(mapAcctDetails(dto.getDetails(), \"tlastInd\"))")
    AFBDSubAcctListSGDTO  mapToSubSummarySGResp(ESBAFBDBlockDetailDTO dto);

    @Mapping(target = "acctNumber", expression = "java(mapSGAcctNumber(dto.getAccountNumber()))")
    @Mapping(target = "outstandingBalance", expression = "java(mapAcctDetailsSG(dto.getDetails(), \"oustandingBalance\"))")
    @Mapping(target = "financedAmt", expression = "java(mapAcctDetailsSG(dto.getDetails(), \"financedAmount\"))")
    @Mapping(target = "instalmentAmt", expression = "java(mapAcctDetailsSG(dto.getDetails(), \"installmentAmount\"))")
    @Mapping(target = "settlementAmount", expression = "java(mapAcctDetailsSG(dto.getDetails(), \"settlementAmount\"))")
    @Mapping(target = "instalmentAmtOverdue", expression = "java(mapAcctDetailsSG(dto.getDetails(), \"overdueInstalmentAmount\"))")
    @Mapping(target = "tlastIndicator", expression = "java(mapAcctDetails(dto.getDetails(), \"tlastInd\"))")
    @Mapping(source = "region", target = "provider")
    AFBDAcctListViewAcctDTO mapToSummarySGResp(ESBAFBDAccountDTO dto);

    @Named("mapAcctDetails")
    default String mapAcctDetails(List<ESBAFBDAccountDetailDTO> details, String acctDetail) {
        if (details == null || acctDetail == null)
            return null;

        String value = details.stream()
                .filter(detail -> detail != null && detail.getName() != null && acctDetail.equalsIgnoreCase(detail.getName()))
                .map(detail -> detail.getValue() != null ? detail.getValue() : "")
                .findFirst()
                .orElse(null);

        return ESBValueUtil.removeLeadingPlus(value);
    }

    @Named("mapSGAcctNumber")
    default String mapSGAcctNumber(String accountNumber) {
        if (accountNumber == null)
            return null;

        return ESBValueUtil.removeLeadingZeroes(accountNumber);
    }

    @Named("mapAcctDetailsSG")
    default String mapAcctDetailsSG(List<ESBAFBDAccountDetailDTO> details, String acctDetail) {
        if (details == null || acctDetail == null)
            return null;

        return details.stream()
                .filter(detail -> detail != null && detail.getName() != null && acctDetail.equalsIgnoreCase(detail.getName()))
                .map(detail -> detail.getValue() != null ? ESBValueUtil.formatSgEsbAmountValue(detail.getValue()) : "")
                .findFirst()
                .orElse(null);
    }

    @Named("mapDebitNote")
    default String mapDebitNote(List<List<ESBAFBDAccountDetailDTO>> details, String acctDetail) {
        if (details == null)
            return null;

        return details.stream()
                .flatMap(List::stream)
                .filter(detail -> detail != null && detail.getName() != null && acctDetail.equalsIgnoreCase(detail.getName()))
                .map(detail -> detail.getValue() != null ? detail.getValue() : "")
                .findFirst()
                .orElse(null);
    }

    @Named("mapDebitNoteDate")
    default String mapDebitNoteDate(List<List<ESBAFBDAccountDetailDTO>> details, String acctDetail) {
        if (details == null || acctDetail == null)
            return null;

        String date = details.stream()
                .flatMap(List::stream)
                .filter(detail -> detail != null && detail.getName() != null && acctDetail.equalsIgnoreCase(detail.getName()))
                .map(detail -> detail.getValue() != null ? detail.getValue() : "")
                .findFirst()
                .orElse(null);

        return ESBValueUtil.convertDateFormat(date);
    }

    default List<AFBDDebitNoteListDTO> mapToDebitNoteListResp(ESBAFBDDebitNoteDetailDTO dto) {
        if (dto == null || dto.getDetails() == null) return java.util.Collections.emptyList();
        return dto.getDetails().stream()
                .map(this::mapDetailRowToDebitNote)
                .filter(Objects::nonNull)
                .toList();
    }

    default AFBDDebitNoteListDTO mapDetailRowToDebitNote(List<ESBAFBDAccountDetailDTO> detailRow) {
        if (detailRow == null) return null;
        AFBDDebitNoteListDTO out = new AFBDDebitNoteListDTO();
        out.setAcctNumber(extract(detailRow, "blockNo"));
        out.setBlockAmt(extract(detailRow, "blockAmount"));
        out.setDisbursementDate(extractDate(detailRow, "dateReimbursed"));
        return out;
    }

    default String extract(List<ESBAFBDAccountDetailDTO> row, String key) {
        if (row == null) return null;

        return row.stream()
                .filter(it -> it != null && it.getName() != null && it.getName().equalsIgnoreCase(key))
                .map(ESBAFBDAccountDetailDTO::getValue)
                .findFirst()
                .orElse(null);
    }

    default String extractDate(List<ESBAFBDAccountDetailDTO> row, String acctDetail) {
        if (row == null) return null;

        String rowDate = extract(row,acctDetail);
        return rowDate == null ? null : ESBValueUtil.convertDateFormat(rowDate);
    }

}