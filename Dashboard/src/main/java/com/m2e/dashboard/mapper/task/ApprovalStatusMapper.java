package com.m2e.dashboard.mapper.task;

import com.m2e.dashboard.dto.task.response.TaskApprovalStatusDTO;
import com.m2e.dashboard.projection.ApprovalStatusProjection;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ApprovalStatusMapper {

    ApprovalStatusMapper INSTANCE = Mappers.getMapper(ApprovalStatusMapper.class);

    @Mappings({
            @Mapping(source = "description", target = "desc")
    })
    TaskApprovalStatusDTO toDTO(ApprovalStatusProjection projection);

    List<TaskApprovalStatusDTO> toDTOList(List<ApprovalStatusProjection> projections);
}
