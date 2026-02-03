package com.m2e.dashboard.mapper.maintenance;

import com.m2e.dashboard.dto.maintenance.response.SubProductDTO;
import com.m2e.dashboard.dto.task.response.TaskApprovalStatusDTO;
import com.m2e.dashboard.projection.AccountTypeProjection;
import com.m2e.dashboard.projection.ApprovalStatusProjection;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AccountTypeMapper {

    AccountTypeMapper INSTANCE = Mappers.getMapper(AccountTypeMapper.class);

    @Mappings({
            @Mapping(source = "id", target = "subProductId"),
            @Mapping(source = "code", target = "subProductCode"),
            @Mapping(source = "description", target = "subProductDesc")
    })
    SubProductDTO toDTO(AccountTypeProjection projection);

    List<SubProductDTO> toDTOList(List<AccountTypeProjection> projections);
}
