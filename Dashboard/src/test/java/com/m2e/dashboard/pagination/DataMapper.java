package com.m2e.dashboard.pagination;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface DataMapper {

    DataMapper INSTANCE = Mappers.getMapper(DataMapper.class);

    TargetDTO mapToTargetDTO(SourceDTO dto);
}
