package com.m2e.dashboard.service.loan.tlf.jms.account.activityinquiry;

import com.m2e.dashboard.config.jms.ESBIntegrationProperties;
import com.m2e.dashboard.dto.loan.tlf.request.LNTrxHistoryReqDTO;
import com.m2e.dashboard.enums.PropertyServiceEnum;
import com.m2e.dashboard.jms.common.ESBPropertiesDTO;
import com.m2e.dashboard.jms.common.request.ESBDTRangeReqDTO;
import com.m2e.dashboard.jms.common.request.ESBMsgReqDTO;
import com.m2e.dashboard.jms.common.request.ESBReqDTO;
import com.m2e.dashboard.jms.loan.tlf.request.account.activityinquiry.ESBLoanTLFAcctActivityInquiryMsgBodyReqDTO;
import com.m2e.dashboard.service.esb.client.ESBClient;
import com.m2e.dashboard.service.esb.ESBRequestHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Objects;

import static com.m2e.dashboard.enums.ESBServiceType.LOAN_TLF_ACCT_ACTIVITY_INQUIRY_MY_FOREIGN_CONV;
import static com.m2e.dashboard.util.DateTimeUtil.*;
import static com.m2e.dashboard.util.DateTimeUtil.getESBDefaultTime;

@Service
public class LoanTLFAcctActivityInquiryMYForeignConvRequestHandler implements ESBRequestHandler<LNTrxHistoryReqDTO, ESBReqDTO> {
    private static final Logger log = LoggerFactory.getLogger(LoanTLFAcctActivityInquiryMYForeignConvRequestHandler.class);

    private final ESBIntegrationProperties esbIntegrationProperties;
    private final ESBClient esbClient;

    @Autowired
    public LoanTLFAcctActivityInquiryMYForeignConvRequestHandler(ESBIntegrationProperties esbIntegrationProperties, ESBClient esbClient) {
        this.esbIntegrationProperties = esbIntegrationProperties;
        this.esbClient = esbClient;
    }

    @Override
    public ESBReqDTO prepareRequest(LNTrxHistoryReqDTO request) {
        ESBPropertiesDTO configProperties = esbIntegrationProperties.getIbmMq(PropertyServiceEnum.LOAN_TLF_GETACCTACTIVITYINQUIRY_FOREIGN_MY_CONV.getValue());
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

        return ESBReqDTO.builder()
                .message(ESBMsgReqDTO.builder()
                        .messageHeader(esbClient.constructMsgHeader(configProperties))
                        .messageBody(ESBLoanTLFAcctActivityInquiryMsgBodyReqDTO.builder()
                                .accountNumber(request.getAcctNumber())
                                .accountType("L")
                                .currency(request.getCurrency())
                                .mbbLastKey("0")
                                .dtRange(dtRange)
                                .build())
                        .build())
                .build();
    }

    @Override
    public String getServiceType() {
        return LOAN_TLF_ACCT_ACTIVITY_INQUIRY_MY_FOREIGN_CONV.name();
    }
}
