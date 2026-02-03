package com.m2e.dashboard.config.jms;

import com.ibm.mq.jakarta.jms.MQQueueConnectionFactory;
import com.ibm.msg.client.jakarta.wmq.common.CommonConstants;
import com.m2e.dashboard.enums.PropertyServiceEnum;
import com.m2e.dashboard.jms.common.MQPropertiesDTO;
import jakarta.jms.ConnectionFactory;
import jakarta.jms.JMSException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.core.JmsTemplate;

import java.net.MalformedURLException;
import java.net.URL;

@Configuration
public class ESBConfig {

    private static final Logger log = LoggerFactory.getLogger(ESBConfig.class);

    private final MQIntegrationProperties mqIntegrationProperties;

    @Autowired
    public ESBConfig(MQIntegrationProperties mqIntegrationProperties) {
        this.mqIntegrationProperties = mqIntegrationProperties;
    }

    @Bean
    public ConnectionFactory mqFactoryNode() {
        MQQueueConnectionFactory factory = null;
        try {
            factory = new MQQueueConnectionFactory();

            log.info("#### CCDT ####");
            log.info("ccdtUrl : {} ", jmsProperties().getCcdt().getUrl());
            log.info("mqManagerNode : {} ", jmsProperties().getQueueManager().getNode());

            URL url = new URL("file://" + jmsProperties().getCcdt().getUrl());
            factory.setQueueManager(jmsProperties().getQueueManager().getNode());
            factory.setTransportType(CommonConstants.WMQ_CM_CLIENT);
            factory.setCCDTURL(url);
        } catch (JMSException | MalformedURLException e) {
            log.error(e.getMessage(), e);
        }
        return factory;
    }

    @Bean
    public JmsTemplate jmsTemplate() {
        JmsTemplate template = new JmsTemplate();
        template.setConnectionFactory(mqFactoryNode());
        template.setDefaultDestinationName(jmsProperties().getMyQueue().getRequest());
        template.setReceiveTimeout(jmsProperties().getTimeout());
        return template;
    }

    private MQPropertiesDTO jmsProperties() {
        return mqIntegrationProperties.getIbmMq(PropertyServiceEnum.ESB.getValue());
    }
}
