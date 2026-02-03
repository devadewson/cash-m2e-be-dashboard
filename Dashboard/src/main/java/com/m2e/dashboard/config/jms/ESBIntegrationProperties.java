package com.m2e.dashboard.config.jms;

import com.m2e.dashboard.jms.common.ESBPropertiesDTO;
import com.m2e.dashboard.mapper.ESBIntegrationMapper;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@ConfigurationProperties(prefix = "m2e.integration")
@Data
@Slf4j
public class ESBIntegrationProperties {

    public final Map<String, Map<String, Object>> ibmMQ;
    public final Map<String, ESBPropertiesDTO> ibmMQMap = new HashMap<>();

    public ESBPropertiesDTO getIbmMq(String serviceName){
        return ibmMQMap.get(serviceName);
    }

    @PostConstruct
    public void init() {
        ibmMQ.forEach((key, map) -> {
            ESBPropertiesDTO configDTO = ESBIntegrationMapper.INSTANCE.mapPropertiesToESBIntegrationConfigDTO(map);
            ibmMQMap.put(key, configDTO);
        });
    }
}
