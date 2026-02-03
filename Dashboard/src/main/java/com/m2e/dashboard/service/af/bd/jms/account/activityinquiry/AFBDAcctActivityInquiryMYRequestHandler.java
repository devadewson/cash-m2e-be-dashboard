package com.m2e.dashboard.service.af.bd.jms.account.activityinquiry;

import com.m2e.dashboard.config.jms.ESBIntegrationProperties;
import com.m2e.dashboard.enums.PropertyServiceEnum;
import com.m2e.dashboard.jms.af.bd.request.account.activityinquiry.ESBAFBDAcctActivityInquiryMsgBodyReqDTO;
import com.m2e.dashboard.jms.af.common.ESBAFAcctActivityReqDTO;
import com.m2e.dashboard.jms.common.ESBPropertiesDTO;
import com.m2e.dashboard.jms.common.request.ESBDTRangeReqDTO;
import com.m2e.dashboard.jms.common.request.ESBMsgReqDTO;
import com.m2e.dashboard.jms.common.request.ESBReqDTO;
import com.m2e.dashboard.service.esb.client.ESBClient;
import com.m2e.dashboard.service.esb.ESBRequestHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Objects;

import static com.m2e.dashboard.enums.ESBServiceType.AF_BD_ACCT_ACTIVITY_INQUIRY_MY;
import static com.m2e.dashboard.util.DateTimeUtil.*;

@Service
public class AFBDAcctActivityInquiryMYRequestHandler implements ESBRequestHandler<ESBAFAcctActivityReqDTO, ESBReqDTO> {
    private static final Logger log = LoggerFactory.getLogger(AFBDAcctActivityInquiryMYRequestHandler.class);

    private final ESBIntegrationProperties esbIntegrationProperties;
    private final ESBClient esbClient;

    @Autowired
    public AFBDAcctActivityInquiryMYRequestHandler(ESBIntegrationProperties esbIntegrationProperties, ESBClient esbClient) {
        this.esbIntegrationProperties = esbIntegrationProperties;
        this.esbClient = esbClient;
    }

    @Override
    public ESBReqDTO prepareRequest(ESBAFAcctActivityReqDTO request) {
        ESBPropertiesDTO configProperties = esbIntegrationProperties.getIbmMq(PropertyServiceEnum.AF_BD_GETACCTACTIVITYINQUIRY_MY.getValue());
        log.info("Properties info for getActivityInquiry ESB: {}", configProperties);

        ESBDTRangeReqDTO dtRange = ESBDTRangeReqDTO.builder()
                .startDate(Objects.nonNull(request.getFilters()) && (StringUtils.hasText(request.getFilters().getStartDate()))
                        ? getESBDate(request.getFilters().getStartDate())
                        : getESBDefaultDate().get("START_DT"))
                .endDate(Objects.nonNull(request.getFilters()) && (StringUtils.hasText(request.getFilters().getEndDate()))
                        ? getESBDate(request.getFilters().getEndDate())
                        : getESBDefaultDate().get("END_DT"))
                .startTime(Objects.nonNull(request.getFilters()) && (StringUtils.hasText(request.getFilters().getStartTime()))
                        ? getESBTime(request.getFilters().getStartTime())
                        : getESBDefaultTime().get("START_TM"))
                .endTime(Objects.nonNull(request.getFilters()) && (StringUtils.hasText(request.getFilters().getEndTime()))
                        ? getESBTime(request.getFilters().getEndTime())
                        : getESBDefaultTime().get("END_TM"))
                .build();

        ESBAFBDAcctActivityInquiryMsgBodyReqDTO messageBody = ESBAFBDAcctActivityInquiryMsgBodyReqDTO.builder()
                .accountNumber(request.getAcctNumber())
                .accountType(request.getAcctType())
                .currency(request.getCurrency())
                .mbbLastKey("0")
                .dtRange(dtRange)
                .build();

        ESBMsgReqDTO message = ESBMsgReqDTO.builder()
                .messageHeader(esbClient.constructMsgHeader(configProperties))
                .messageBody(messageBody)
                .build();

        return ESBReqDTO.builder()
                .message(message)
                .build();
    }

    @Override
    public String getServiceType() {
        return AF_BD_ACCT_ACTIVITY_INQUIRY_MY.name();
    }
}
