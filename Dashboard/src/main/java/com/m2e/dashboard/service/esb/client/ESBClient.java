package com.m2e.dashboard.service.esb.client;

import com.m2e.dashboard.enums.PropertyServiceEnum;
import com.m2e.dashboard.jms.common.ESBPropertiesDTO;
import com.m2e.dashboard.jms.common.request.ESBMsgHeaderReqDTO;

public interface ESBClient {
    String callHost(String requestString, PropertyServiceEnum queueName);

    ESBMsgHeaderReqDTO constructMsgHeader(ESBPropertiesDTO properties);
}
