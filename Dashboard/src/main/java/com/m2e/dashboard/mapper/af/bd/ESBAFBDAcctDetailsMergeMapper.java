package com.m2e.dashboard.mapper.af.bd;

import com.m2e.dashboard.dto.AccountConditions;
import com.m2e.dashboard.dto.af.response.bd.*;
import com.m2e.dashboard.mapper.BaseMapper;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface ESBAFBDAcctDetailsMergeMapper extends BaseMapper {
    @Mapping(target = "financedAmt")
    @Mapping(target = "outstandingBalance", qualifiedByName = NULL_IF_CLOSED)
    @Mapping(target = "facilityLimit", qualifiedByName = NULL_IF_CLOSED)
    @Mapping(target = "esbMetadata")
    @Mapping(target = "islamic", source = "islamicFlag", qualifiedByName = Y_OR_N_TO_BOOLEAN)
    @BeanMapping(ignoreByDefault = true)
    void merge(@MappingTarget AFBDMasterAcctDetailDTO tMinusOneDataOrSpData,
               AFBDMasterAcctDetailDTO tDataOrEsbData,
               @Context AccountConditions accountConditions);

    @Mapping(target = "financedAmt", qualifiedByName = NULL_IF_CLOSED)
    @Mapping(target = "outstandingBalance", qualifiedByName = NULL_IF_CLOSED)
    @Mapping(target = "numberOfInstalmentPaid")
    @Mapping(target = "numberOfInstalmentInArreas")
    @Mapping(target = "installmentAmount")
    @Mapping(target = "amountDue")
    @Mapping(target = "dueDate")
    @Mapping(target = "settlementAmount")
    @Mapping(target = "islamic", source = "islamicFlag", qualifiedByName = Y_OR_N_TO_BOOLEAN)
    @Mapping(target = "esbMetadata")
    @BeanMapping(ignoreByDefault = true)
    void merge(@MappingTarget AFBDAcctDetailDTO tMinusOneDataOrSpData,
               AFBDAcctDetailDTO tDataOrEsbData,
               @Context AccountConditions accountConditions);

    @Mapping(target = "financedAmt", qualifiedByName = NULL_IF_CLOSED)
    @Mapping(target = "outstandingBalance", qualifiedByName = NULL_IF_CLOSED)
    @Mapping(target = "utilisedAmt")
    @Mapping(target = "numberOfInstalmentPaid")
    @Mapping(target = "numberOfInstalmentInArreas")
    @Mapping(target = "installmentAmount")
    @Mapping(target = "amountDue")
    @Mapping(target = "dueDate")
    @Mapping(target = "settlementAmount")
    @Mapping(target = "esbMetadata")
    @Mapping(target = "islamic", source = "islamicFlag", qualifiedByName = Y_OR_N_TO_BOOLEAN)
    @BeanMapping(ignoreByDefault = true)
    void merge(@MappingTarget AFBDSubAcctDetailDTO tMinusOneDataOrSpData,
               AFBDSubAcctDetailDTO tDataOrEsbData,
               @Context AccountConditions accountConditions);

    @Mapping(target = "financedAmt", qualifiedByName = NULL_IF_CLOSED)
    @Mapping(target = "outstandingBalance", qualifiedByName = NULL_IF_CLOSED_OR_TLAST)
    @Mapping(target = "numberOfInstalmentPaid", qualifiedByName = NULL_IF_CLOSED)
    @Mapping(target = "numberOfInstalmentInArreas")
    @Mapping(target = "dueDate", qualifiedByName = NULL_IF_CLOSED)
    @Mapping(target = "instalmentAmt", qualifiedByName = NULL_IF_CLOSED_OR_TLAST)
    @Mapping(target = "amountDue", qualifiedByName = NULL_IF_CLOSED)
    @Mapping(target = "interestRate", qualifiedByName = NULL_IF_CLOSED)
    @Mapping(target = "originalLoanAmt", qualifiedByName = NULL_IF_CLOSED)
    @Mapping(target = "originalTenure", qualifiedByName = NULL_IF_CLOSED)
    @Mapping(target = "remainingTenure", qualifiedByName = NULL_IF_CLOSED)
    @Mapping(target = "lateCharges", qualifiedByName = NULL_IF_CLOSED)
    @Mapping(target = "misc", qualifiedByName = NULL_IF_CLOSED)
    @Mapping(target = "otherCharges", qualifiedByName = NULL_IF_CLOSED)
    @Mapping(target = "settlementAmount")
    @Mapping(target = "instalmentAmtOverdue", qualifiedByName = NULL_IF_CLOSED)
    @Mapping(target = "islamic", source = "islamicFlag", qualifiedByName = Y_OR_N_TO_BOOLEAN)
    @Mapping(target = "tlastIndicator")
    @Mapping(target = "esbMetadata")
    @BeanMapping(ignoreByDefault = true)
    void merge(@MappingTarget AFBDSGAcctDetailDTO tMinusOneDataOrSpData,
               AFBDSGAcctDetailDTO tDataOrEsbData,
               @Context AccountConditions accountConditions);

    @Mapping(target = "outstandingLoan", qualifiedByName = NULL_IF_CLOSED_OR_TLAST)
    @Mapping(target = "instalmentAmt", qualifiedByName = NULL_IF_CLOSED_OR_TLAST)
    @Mapping(target = "amountDue")
    @Mapping(target = "interestRate", qualifiedByName = NULL_IF_CLOSED)
    @Mapping(target = "dueDate", qualifiedByName = NULL_IF_CLOSED)
    @Mapping(target = "originalTenure", qualifiedByName = NULL_IF_CLOSED)
    @Mapping(target = "numberOfInstalmentPaid", qualifiedByName = NULL_IF_CLOSED)
    @Mapping(target = "remainingTenure", qualifiedByName = NULL_IF_CLOSED)
    @Mapping(target = "lateInterest", qualifiedByName = NULL_IF_CLOSED)
    @Mapping(target = "misc", qualifiedByName = NULL_IF_CLOSED)
    @Mapping(target = "otherCharges", qualifiedByName = NULL_IF_CLOSED)
    @Mapping(target = "chassisNumber")
    @Mapping(target = "engineNumber")
    @Mapping(target = "collateralDescription")
    @Mapping(target = "originalLoanAmt", qualifiedByName = NULL_IF_CLOSED)
    @Mapping(target = "settlementAmount")
    @Mapping(target = "numberOfSubAcct")
    @Mapping(target = "instalmentAmtOverdue", qualifiedByName = NULL_IF_CLOSED)
    @Mapping(target = "islamic", source = "islamicFlag", qualifiedByName = Y_OR_N_TO_BOOLEAN)
    @Mapping(target = "tlastIndicator")
    @Mapping(target = "esbMetadata")
    @BeanMapping(ignoreByDefault = true)
    void merge(@MappingTarget AFBDSGSubAcctDetailDTO tMinusOneDataOrSpData,
               AFBDSGSubAcctDetailDTO tDataOrEsbData,
               @Context AccountConditions accountConditions);
}
