package com.m2e.dashboard.service.deposit.ca.jms.account.activityinquiry.my;

import com.m2e.dashboard.config.jms.ESBIntegrationProperties;
import com.m2e.dashboard.enums.PropertyServiceEnum;
import com.m2e.dashboard.jms.common.ESBPropertiesDTO;
import com.m2e.dashboard.jms.common.request.ESBDTRangeReqDTO;
import com.m2e.dashboard.jms.common.request.ESBMsgReqDTO;
import com.m2e.dashboard.jms.common.request.ESBReqDTO;
import com.m2e.dashboard.jms.deposit.ca.request.account.activityinquiry.ESBDepositCAAcctActivityInquiryMsgBodyReqDTO;
import com.m2e.dashboard.jms.deposit.ca.request.account.activityinquiry.ESBDepositCAAcctActivityReqDTO;
import com.m2e.dashboard.service.esb.client.ESBClient;
import com.m2e.dashboard.service.esb.ESBRequestHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Objects;

import static com.m2e.dashboard.enums.ESBServiceType.DEPOSIT_CA_ACCT_ACTIVITY_INQUIRY_MY;
import static com.m2e.dashboard.util.DateTimeUtil.*;
import static com.m2e.dashboard.util.DateTimeUtil.getESBDefaultTime;

@Service
public class DepositCAAcctActivityInquiryMYRequestHandler implements ESBRequestHandler<ESBDepositCAAcctActivityReqDTO, ESBReqDTO> {
    private static final Logger log = LoggerFactory.getLogger(DepositCAAcctActivityInquiryMYRequestHandler.class);

    private final ESBIntegrationProperties esbIntegrationProperties;
    private final ESBClient esbClient;

    @Autowired
    public DepositCAAcctActivityInquiryMYRequestHandler(ESBIntegrationProperties esbIntegrationProperties, ESBClient esbClient) {
        this.esbIntegrationProperties = esbIntegrationProperties;
        this.esbClient = esbClient;
    }
    @Override
    public ESBReqDTO prepareRequest(ESBDepositCAAcctActivityReqDTO request) {
        ESBPropertiesDTO configProperties = esbIntegrationProperties.getIbmMq(PropertyServiceEnum.DEPOSIT_CA_GETACCTACTIVITYINQUIRY_MY.getValue());
        log.info("Properties info for CA getActivityInquiry ESB: {}", configProperties);

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

        ESBDepositCAAcctActivityInquiryMsgBodyReqDTO messageBody = ESBDepositCAAcctActivityInquiryMsgBodyReqDTO.builder()
                .accountNumber(request.getAccountNo())
                .accountType("D")
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
        return DEPOSIT_CA_ACCT_ACTIVITY_INQUIRY_MY.name();
    }
}
