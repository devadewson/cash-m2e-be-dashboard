package com.m2e.dashboard.mapper.loan.tlf;

import com.m2e.dashboard.dto.loan.tlf.LNTLFAccountType;
import com.m2e.dashboard.dto.loan.tlf.response.LNTLFDTO;
import com.m2e.dashboard.mapper.BaseMapper;
import com.m2e.dashboard.sp.loan.tlf.response.LNTLFAcctListingSpDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface LoanTLFMapper extends BaseMapper {
    String TO_ACCOUNT_TYPE_INDICATORS = "toAccountTypeIndicators";

    @Mapping(target = "accountTypeIndicators", source = ".", qualifiedByName = TO_ACCOUNT_TYPE_INDICATORS)
    @Mapping(target = "isOverdue", source = "isOverdue", qualifiedByName = Y_OR_N_TO_BOOLEAN)
    @Mapping(target = "hasSellingPrice", source = "hasSellingPrice", qualifiedByName = Y_OR_N_TO_BOOLEAN)
    LNTLFDTO toLnTlfDto(LNTLFAcctListingSpDTO spResult);

    @Named(TO_ACCOUNT_TYPE_INDICATORS)
    default LNTLFAccountType toAccountTypeIndicators(LNTLFAcctListingSpDTO spResult) {
        return new LNTLFAccountType(
                spResult.getProvider(),
                yOrNToBoolean(spResult.getIsForeign()),
                yOrNToBoolean(spResult.getIsIslamic()),
                yOrNToBoolean(spResult.getIsFitas())
        );
    }
}
