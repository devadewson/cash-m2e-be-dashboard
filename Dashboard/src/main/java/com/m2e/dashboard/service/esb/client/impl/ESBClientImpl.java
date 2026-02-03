package com.m2e.dashboard.service.esb.client.impl;

import com.ibm.mq.jakarta.jms.MQQueue;
import com.ibm.msg.client.jakarta.wmq.common.CommonConstants;
import com.m2e.dashboard.config.jms.MQIntegrationProperties;
import com.m2e.dashboard.enums.PropertyServiceEnum;
import com.m2e.dashboard.jms.common.ESBPropertiesDTO;
import com.m2e.dashboard.jms.common.MQPropertiesDTO;
import com.m2e.dashboard.jms.common.request.ESBMsgHeaderReqDTO;
import com.m2e.dashboard.service.esb.client.ESBClient;
import com.m2e.dashboard.util.DateTimeUtil;
import jakarta.jms.*;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.util.UUID;

import static com.ibm.msg.client.jakarta.jms.JmsConstants.JMS_IBM_CHARACTER_SET;

@ConditionalOnProperty(name = "m2e.implementation", havingValue = "production", matchIfMissing = true)
@Slf4j
@RequiredArgsConstructor
@Component
public class ESBClientImpl implements ESBClient {
    private final JmsTemplate jmsTemplate;
    private final MQIntegrationProperties mqIntegrationProperties;

    @Override
    public String callHost(String requestString, PropertyServiceEnum queueName) {
        MQPropertiesDTO jmsProperties = mqIntegrationProperties.getIbmMq(PropertyServiceEnum.ESB.getValue());
        log.debug("ESB Request Body: {}", requestString);
        String requestQueue = mqIntegrationProperties.findRequestQueue(jmsProperties, queueName);
        log.debug("MQ REQUEST QUEUE: {}", requestQueue);
        String responseQueue = mqIntegrationProperties.findResponseQueue(jmsProperties, queueName);
        log.debug("MQ RESPONSE QUEUE: {}", responseQueue);

        try {
            Message responseMessage = sendMessageAndReceiveResponse(requestString, jmsProperties, queueName);

            if (responseMessage == null) {
                log.error("ESB Timeout for: {}", requestString);
                return null;
            }

            return extractMessageBody(responseMessage);
        } catch (Exception e) {
            log.error("Error processing ESB request: {} -> {} ", e.getMessage(), e);
            return null;
        }
    }

    private Message sendMessageAndReceiveResponse(String requestString, MQPropertiesDTO esbConfig, PropertyServiceEnum queueName) {
        MQQueue serviceRequestQueue = createServiceRequestQueue(esbConfig, queueName);

        return jmsTemplate.execute(session -> {
            MessageProducer producer = session.createProducer(serviceRequestQueue);
            Message message = createMessage(session, requestString, esbConfig, queueName);
            producer.send(message);

            MessageConsumer consumer = createConsumer(session, esbConfig, message.getJMSMessageID(), queueName);
            Message response = consumer.receive(esbConfig.getTimeout());

            closeResources(session, producer, consumer, response);
            return response;
        }, true);
    }

    @SneakyThrows
    private MQQueue createServiceRequestQueue(MQPropertiesDTO esbConfig, PropertyServiceEnum queueName) {
        MQQueue serviceRequestQueue = new MQQueue(mqIntegrationProperties.findRequestQueue(esbConfig, queueName));
        serviceRequestQueue.setTargetClient(CommonConstants.WMQ_CLIENT_NONJMS_MQ);
        return serviceRequestQueue;
    }

    private Message createMessage(Session session, String requestString, MQPropertiesDTO esbConfig, PropertyServiceEnum queueName) throws JMSException {
        TextMessage message = session.createTextMessage();
        message.setText(requestString);
        message.setJMSReplyTo(session.createQueue(mqIntegrationProperties.findResponseQueue(esbConfig, queueName)));
        message.setJMSCorrelationID(UUID.randomUUID().toString());
        return message;
    }

    private MessageConsumer createConsumer(Session session, MQPropertiesDTO esbConfig, String msgId, PropertyServiceEnum queueName) throws JMSException {
        return session.createConsumer(jmsTemplate.getDestinationResolver()
                .resolveDestinationName(session, mqIntegrationProperties.findResponseQueue(esbConfig, queueName), false), "JMSCorrelationID='" + msgId + "'");
    }

    private void closeResources(Session session, MessageProducer producer, MessageConsumer consumer, Message response) throws JMSException {
        try {
            if (response != null) {
                response.acknowledge();
            }
        } finally {
            if (producer != null) producer.close();
            if (consumer != null) consumer.close();
            if (session != null) session.close();
        }
    }

    private String extractMessageBody(Message responseMessage) throws JMSException, UnsupportedEncodingException {
        try {
            return responseMessage.getBody(String.class).replace("\n", " ");
        } catch (Exception e) {
            return extractBytesMessageBody((BytesMessage) responseMessage);
        }
    }

    private String extractBytesMessageBody(BytesMessage message) throws JMSException, UnsupportedEncodingException {
        message.reset();
        byte[] msgBytes = new byte[(int) message.getBodyLength()];
        message.readBytes(msgBytes);
        String encoding = message.getStringProperty(JMS_IBM_CHARACTER_SET);
        String textMessageBody = new String(msgBytes, encoding).trim();
        log.debug(textMessageBody);
        return textMessageBody;
    }

    @Override
    public ESBMsgHeaderReqDTO constructMsgHeader(ESBPropertiesDTO properties) {
        return new ESBMsgHeaderReqDTO(
                properties.getEsbVer(),
                properties.getEsbSvcId(),
                properties.getEsbTxnCode(),
                properties.getEsbEnv(),
                properties.getEsbMsgId() + DateTimeUtil.getCompactDateTime());
    }
}
