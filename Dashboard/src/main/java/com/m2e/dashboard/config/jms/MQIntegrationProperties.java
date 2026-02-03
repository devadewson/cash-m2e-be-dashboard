package com.m2e.dashboard.config.jms;

import com.m2e.dashboard.enums.PropertyServiceEnum;
import com.m2e.dashboard.jms.common.MQPropertiesDTO;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@ConfigurationProperties(prefix = "m2e.config.queue")
@Data
public class MQIntegrationProperties {

    private Map<String, MQPropertiesDTO> ibmmq;

    public MQPropertiesDTO getIbmMq(String serviceName) {
        return ibmmq.get(serviceName);
    }

    public String findResponseQueue(MQPropertiesDTO properties, PropertyServiceEnum queueName) {
        String responseQueue;
        if (queueName.equals(PropertyServiceEnum.ESBSG)) {
            responseQueue = properties.getSgQueue().getResponse();
        } else if (queueName.equals(PropertyServiceEnum.ESBMYV23)) {
            responseQueue = properties.getMyv23Queue().getResponse();
        } else {
            responseQueue = properties.getMyQueue().getResponse();
        }
        return responseQueue;
    }

    public String findRequestQueue(MQPropertiesDTO properties, PropertyServiceEnum queueName) {
        String requestQueue;
        if (queueName.equals(PropertyServiceEnum.ESBSG)) {
            requestQueue = properties.getSgQueue().getRequest();
        } else if (queueName.equals(PropertyServiceEnum.ESBMYV23)) {
            requestQueue = properties.getMyv23Queue().getRequest();
        } else {
            requestQueue = properties.getMyQueue().getRequest();
        }
        return requestQueue;
    }
}
