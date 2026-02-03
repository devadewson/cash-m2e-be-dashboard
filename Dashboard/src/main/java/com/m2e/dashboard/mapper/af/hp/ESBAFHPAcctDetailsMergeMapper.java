package com.m2e.dashboard.mapper.af.hp;

import com.m2e.dashboard.dto.AccountConditions;
import com.m2e.dashboard.dto.af.response.hp.AFHPSGAcctDetailDTO;
import com.m2e.dashboard.mapper.BaseMapper;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface ESBAFHPAcctDetailsMergeMapper extends BaseMapper {
    @Mapping(target = "otherCharges", qualifiedByName = NULL_IF_CLOSED)
    @Mapping(target = "outstandingBalance", qualifiedByName = NULL_IF_CLOSED)
    @Mapping(target = "settlementAmt", qualifiedByName = NULL_IF_CLOSED)
    @Mapping(target = "instalmentDue", qualifiedByName = NULL_IF_CLOSED)
    @Mapping(target = "monthlyInstalment", qualifiedByName = NULL_IF_TLAST)
    @Mapping(target = "interestRate", qualifiedByName = NULL_IF_CLOSED)
    @Mapping(target = "dueDate")
    @Mapping(target = "numberOfInstalmentPaid")
    @Mapping(target = "amountDue")
    @Mapping(target = "instalmentAmt", qualifiedByName = NULL_IF_CLOSED_OR_TLAST)
    @Mapping(target = "originalLoanAmt", qualifiedByName = NULL_IF_CLOSED)
    @Mapping(target = "originalTenure")
    @Mapping(target = "remainingTenure")
    @Mapping(target = "collateralInformation")
    @Mapping(target = "collateralIDCarPlate")
    @Mapping(target = "collateralDescription")
    @Mapping(target = "chassisNumber")
    @Mapping(target = "engineNumber")
    @Mapping(target = "lateCharges", qualifiedByName = NULL_IF_CLOSED)
    @Mapping(target = "miscOutOfPocket", qualifiedByName = NULL_IF_CLOSED)
    @Mapping(target = "accruedLateCharges", qualifiedByName = NULL_IF_CLOSED)
    @Mapping(target = "lateInterest")
    @Mapping(target = "tlastIndicator")
    @Mapping(target = "esbMetadata")
    @BeanMapping(ignoreByDefault = true)
    void mergeForProviderSG(@MappingTarget AFHPSGAcctDetailDTO tMinusOneDataOrSpData,
                            AFHPSGAcctDetailDTO tDataOrEsbData,
                            @Context AccountConditions accountConditions);
}
