package com.m2e.dashboard.config.jms;

import com.m2e.dashboard.enums.ESBServiceType;
import com.m2e.dashboard.service.esb.ESBResponseProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.support.AopUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Configuration
public class ESBResponseProcessorConfig {
//
//    private static final Logger log = LoggerFactory.getLogger(ESBResponseProcessorConfig.class);
//
//    private final List<ESBResponseProcessor<?>> allProcessors;
//
//    public ESBResponseProcessorConfig(List<ESBResponseProcessor<?>> allProcessors) {
//        this.allProcessors = allProcessors;
//    }
//
//    @Bean
//    public Map<ESBServiceType, ESBResponseProcessor<?>> responseProcessors() {
//        Map<ESBServiceType, ESBResponseProcessor<?>> processors = new EnumMap<>(ESBServiceType.class);
//
//        for (ESBResponseProcessor<?> processor : allProcessors) {
//            Class<?> targetClass = AopUtils.getTargetClass(processor);
//            ESBServiceMapping mapping = targetClass.getAnnotation(ESBServiceMapping.class);
//            if (mapping != null) {
//                processors.put(mapping.value(), processor);
//            } else {
//                String className = processor.getClass().getName();
//                throw new IllegalStateException("Missing @ESBServiceMapping annotation on processor: " + className);
//            }
//        }
//
//        // Validate that all ESBServiceType values are mapped
//        for (ESBServiceType type : ESBServiceType.values()) {
//            if (!processors.containsKey(type)) {
//                throw new IllegalStateException("No processor registered for ESBServiceType: " + type.name());
//            }
//        }
//
//        log.info("All ESBServiceType values are successfully mapped to processors.");
//        return processors;
//    }
}

