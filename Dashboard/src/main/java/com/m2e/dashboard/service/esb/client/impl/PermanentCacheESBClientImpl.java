package com.m2e.dashboard.service.esb.client.impl;

import com.m2e.dashboard.config.jms.MQIntegrationProperties;
import com.m2e.dashboard.enums.PropertyServiceEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

@ConditionalOnProperty(name = "m2e.implementation", havingValue = "performance-test")
@Slf4j
@Component
public class PermanentCacheESBClientImpl extends ESBClientImpl {
    private static final Map<String, String> cache = new ConcurrentHashMap<>();
    private static final Pattern STATUS_CODE_0 = Pattern.compile("\"StatusCode\":\"0+\"");
    private static final Pattern STATUS_CODE_201 = Pattern.compile("\"StatusCode\":\"201\"");

    public PermanentCacheESBClientImpl(JmsTemplate jmsTemplate, MQIntegrationProperties mqIntegrationProperties) {
        super(jmsTemplate, mqIntegrationProperties);
    }

    @Override
    public String callHost(String requestString, PropertyServiceEnum queueName) {
        String key = generateCacheKey(requestString, queueName);

        String cachedValue = cache.get(key);
        if (cachedValue != null) {
            log.info("Cache HIT for queue: {}, request: {}", queueName, requestString);
            return cachedValue;
        } else {
            log.info("Cache MISS for queue: {}, request: {}", queueName, requestString);
            String result = super.callHost(requestString, queueName);

            if (result != null && (STATUS_CODE_0.matcher(result).find() || STATUS_CODE_201.matcher(result).find())) {
                log.info("Caching 0/201 response for queue: {}, request: {}", queueName, requestString);
                cache.put(key, result);
            }

            return result;
        }
    }

    private String generateCacheKey(String requestString, PropertyServiceEnum queueName) {
        String normalized = requestString.replaceAll("\"MsgID\":\"[^\"]+\"", "\"MsgID\":\"\"");
        String combined = queueName.name() + "::" + normalized;
        log.info("Cache key (pre-SHA-256): {}", combined);
        return DigestUtils.sha256Hex(combined);
    }
}
