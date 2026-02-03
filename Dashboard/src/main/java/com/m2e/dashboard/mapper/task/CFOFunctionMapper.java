package com.m2e.dashboard.mapper.task;

import com.m2e.dashboard.dto.task.response.TaskFunctionDTO;
import com.m2e.dashboard.projection.CFOFunctionProjection;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CFOFunctionMapper {

    CFOFunctionMapper INSTANCE = Mappers.getMapper(CFOFunctionMapper.class);

    @Mappings({
            @Mapping(source = "description", target = "desc")
    })
    TaskFunctionDTO toDTO(CFOFunctionProjection projection);

    List<TaskFunctionDTO> toDTOList(List<CFOFunctionProjection> projections);
}

