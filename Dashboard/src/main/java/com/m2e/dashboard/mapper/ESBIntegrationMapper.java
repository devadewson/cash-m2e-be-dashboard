package com.m2e.dashboard.mapper;

import com.m2e.dashboard.jms.common.ESBPropertiesDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

import java.util.Map;

@Mapper(componentModel = "spring")
public interface ESBIntegrationMapper {

    ESBIntegrationMapper INSTANCE = Mappers.getMapper(ESBIntegrationMapper.class);

    @Mappings({
            @Mapping(target = "esbVer", expression = "java(source.get(\"esb-ver\") != null ? source.get(\"esb-ver\").toString() : null)"),
            @Mapping(target = "esbSvcId", expression = "java(source.get(\"esb-svcid\") != null ? source.get(\"esb-svcid\").toString() : null)"),
            @Mapping(target = "esbTxnCode", expression = "java(source.get(\"esb-txncode\") != null ? source.get(\"esb-txncode\").toString() : null)"),
            @Mapping(target = "esbEnv", expression = "java(source.get(\"esb-env\") != null ? source.get(\"esb-env\").toString() : null)"),
            @Mapping(target = "esbMsgId", expression = "java(source.get(\"esb-msgid\") != null ? source.get(\"esb-msgid\").toString() : null)")
    })
    ESBPropertiesDTO mapPropertiesToESBIntegrationConfigDTO(Map<String, Object> source);
}
