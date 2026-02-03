package com.m2e.dashboard.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.mq.jakarta.jms.MQQueue;
import com.m2e.dashboard.enums.ErrorMessageEnum;
import com.m2e.dashboard.jms.einvoice.response.tviewParty.ESBViewPartyMsgBodyDTO;
import com.m2e.dashboard.jms.einvoice.response.tviewParty.ESBTViewPartyFullMsgBodyDTO;
import com.m2e.dashboard.repository.ErrorMaintenanceRepository;
import com.m2e.model.sso.base.ErrorMaintenance;
import jakarta.annotation.PostConstruct;
import jakarta.jms.*;
import org.apache.commons.lang3.RandomStringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.SessionCallback;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.UUID;

@Component
public class MDMClient {

    @Value("${m2e.config.queue.ibmmq.esb.myv23Queue.request}")
    private String mqRequest;

    @Value("${m2e.config.queue.ibmmq.esb.myv23Queue.response}")
    private String mqResponse;

    @Value("${m2e.config.queue.ibmmq.esb.timeout}")
    private long timeout;

    @Value("${m2e.config.queue.ibmmq.esb.env}")
    private String env;

    @Value("${m2e.config.queue.ibmmq.esb.myver}")
    private String myVer;

    public String getEnv() {
        return env;
    }

    public String getMyVer() {
        return myVer;
    }

    private static final Logger logger = LoggerFactory.getLogger(MDMClient.class);

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JmsTemplate jmsTemplateNode;
    @Autowired
    private ErrorMaintenanceRepository errorMaintenanceRepository;

    @PostConstruct
    public void init() {
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
//    objectMapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
//    objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE);
//    objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
    }

    public String generateMsgId() {
        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyMMddHHmmss");
        LocalDateTime time = LocalDateTime.now();
        String date = time.format(format);

        String final5 = RandomStringUtils.random(5, false, true);
        return "RCM" + date + final5;
    }

    public ESBTViewPartyFullMsgBodyDTO getCifDetail(String jsonString, String appId) {
        String random = UUID.randomUUID().toString();
        try {
            MQQueue serviceRequestQueue = new MQQueue(mqRequest);
            @SuppressWarnings({"unchecked", "rawtypes"})
            Message responseMessage = (Message) jmsTemplateNode.execute(
                    (SessionCallback) session -> {
                        MessageProducer producer = session.createProducer(serviceRequestQueue);
                        TextMessage mess = session.createTextMessage();
                        mess.setText(jsonString);
//            mess.setJMSMessageID(appId);
                        Queue queue = session.createQueue(mqResponse);
                        mess.setJMSReplyTo(queue);
                        mess.setJMSCorrelationID(random);
                        producer.send(mess);
                        logger.info("getCifDetail producer send");
                        String msgId = mess.getJMSMessageID();
                        logger.info("getCifDetail mess.getJMSMessageID()" + mess.getJMSMessageID());
                        producer.close();
                        MessageConsumer consumer = session.createConsumer(jmsTemplateNode.getDestinationResolver().
                                resolveDestinationName(session, mqResponse,
                                        false), "JMSCorrelationID='" + msgId + "'");
                        Message response = consumer.receive(timeout);
                        try {
                            if (response != null) response.acknowledge();
                            consumer.close();
                        } catch (Exception e) {
                            consumer.close();//control will come here.
                            session.close();
                            logger.error(e.getMessage());
                        }
                        session.close();
                        return response;
                    }, true);

            if (responseMessage == null || responseMessage.getBody(String.class) == null) {
                logger.error(ErrorMessageEnum.MSG_ESB_TIMEOUT.getDescription());
                throw new ResponseStatusException(HttpStatus.GATEWAY_TIMEOUT, ErrorMessageEnum.MSG_ACCT_INQ_NOT_FOUND.getDescription());

            }

            logger.info("getCifDetail response JMSCorrelationID=" + responseMessage.getJMSCorrelationID());
            String textMessageBody = responseMessage.getBody(String.class).replaceAll("\n", " ");
            logger.info("getCifDetail applicationID ::" + appId + " textMessageBody ::" + textMessageBody);
            JSONObject jsonRequest = new JSONObject(textMessageBody);

            JSONObject jsonMsgHeader = jsonRequest.getJSONObject("Msg").getJSONObject("MsgHeader");
            JSONArray additionalStatus = jsonMsgHeader.getJSONArray("AdditionalStatusCodes");
            String hostStatusCode = additionalStatus.getJSONObject(0).getString("HostStatusCode");
            String hostStatusDesc = additionalStatus.getJSONObject(0).getString("HostStatusDesc");

            if (!hostStatusCode.equals("M000")) {
                logger.error(hostStatusCode + ": " + hostStatusDesc);
                Optional<ErrorMaintenance> errorMaintenance = errorMaintenanceRepository.findBySystemCode(hostStatusCode);
                if (errorMaintenance.isPresent()) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, errorMaintenance.get().getFeMessage());
                } else {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ErrorMessageEnum.MSG_ACCT_INQ_NOT_FOUND.getDescription());
                }
            }
            ESBViewPartyMsgBodyDTO msg = objectMapper.readValue(textMessageBody, ESBViewPartyMsgBodyDTO.class);
            return msg.getMsg().getMsgBody();
        } catch (JMSException exc) {
            logger.info(exc.getMessage(), exc);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ErrorMessageEnum.MSG_ACCT_INQ_NOT_FOUND.getDescription());
        } catch (JsonProcessingException e) {
            logger.info(e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ErrorMessageEnum.MSG_ACCT_INQ_NOT_FOUND.getDescription());
        }
    }

    public void updatePartyDetails(String jsonString, String appId) {
        String random = UUID.randomUUID().toString();
        try {
            MQQueue serviceRequestQueue = new MQQueue(mqRequest);
            @SuppressWarnings({"unchecked", "rawtypes"})
            Message responseMessage = (Message) jmsTemplateNode.execute(
                    (SessionCallback) session -> {
                        MessageProducer producer = session.createProducer(serviceRequestQueue);
                        TextMessage mess = session.createTextMessage();
                        mess.setText(jsonString);
//            mess.setJMSMessageID(appId);
                        Queue queue = session.createQueue(mqResponse);
                        mess.setJMSReplyTo(queue);
                        mess.setJMSCorrelationID(random);
                        producer.send(mess);
                        logger.info("UpdateParty producer send");
                        String msgId = mess.getJMSMessageID();
                        logger.info("UpdateParty mess.getJMSMessageID()" + mess.getJMSMessageID());
                        producer.close();
                        MessageConsumer consumer = session.createConsumer(jmsTemplateNode.getDestinationResolver().
                                resolveDestinationName(session, mqResponse,
                                        false), "JMSCorrelationID='" + msgId + "'");
                        Message response = consumer.receive(timeout);
                        try {
                            if (response != null) response.acknowledge();
                            consumer.close();
                        } catch (Exception e) {
                            consumer.close();//control will come here.
                            session.close();
                            logger.error(e.getMessage());
                        }
                        session.close();
                        return response;
                    }, true);

            if (responseMessage == null || responseMessage.getBody(String.class) == null) {
                logger.error(ErrorMessageEnum.MSG_ESB_TIMEOUT.getDescription());
                throw new ResponseStatusException(HttpStatus.GATEWAY_TIMEOUT, ErrorMessageEnum.MSG_ACCT_INQ_NOT_FOUND.getDescription());

            }
            logger.info("UpdateParty response JMSCorrelationID=" + responseMessage.getJMSCorrelationID());
            String textMessageBody = responseMessage.getBody(String.class).replaceAll("\n", " ");
            logger.info("UpdateParty applicationID ::" + appId + " Response ::" + textMessageBody);
        } catch (JMSException exc) {
            logger.info(exc.getMessage(), exc);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ErrorMessageEnum.MSG_ACCT_INQ_NOT_FOUND.getDescription());
        }
    }
}
