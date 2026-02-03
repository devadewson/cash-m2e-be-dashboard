package com.m2e.dashboard.service.deposit.ca.jms.account.floatdetails;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.m2e.dashboard.config.jms.ESBIntegrationProperties;
import com.m2e.dashboard.config.jms.ESBServiceMapping;
import com.m2e.dashboard.dto.common.SearchCollectionResponse;
import com.m2e.dashboard.dto.deposit.ca.response.DepositAcctFloatDTO;
import com.m2e.dashboard.dto.deposit.ca.response.DepositAcctFloatDtlDTO;
import com.m2e.dashboard.enums.ESBServiceType;
import com.m2e.dashboard.enums.ESBStatusCodeEnum;
import com.m2e.dashboard.enums.PropertyServiceEnum;
import com.m2e.dashboard.jms.common.ESBPropertiesDTO;
import com.m2e.dashboard.jms.common.request.ESBMsgReqDTO;
import com.m2e.dashboard.jms.common.request.ESBReqDTO;
import com.m2e.dashboard.jms.common.response.ESBDTO;
import com.m2e.dashboard.jms.deposit.ca.request.account.details.ESBDepositCAAcctFloatDetailsReqDTO;
import com.m2e.dashboard.jms.deposit.ca.request.account.details.ESBDepositCAFloatReqDTO;
import com.m2e.dashboard.jms.deposit.ca.response.account.details.ESBDepositCAAccountFloatDetailDTO;
import com.m2e.dashboard.jms.deposit.ca.response.account.details.ESBDepositCAAccountFloatDetailTrnRecDTO;
import com.m2e.dashboard.mapper.deposit.ca.ESBDepositCAAcctFloatDetailsMapper;
import com.m2e.dashboard.service.*;
import com.m2e.dashboard.service.esb.client.ESBClient;
import com.m2e.dashboard.service.esb.ESBMetadataService;
import com.m2e.dashboard.service.esb.ESBResponseProcessor;
import com.m2e.dashboard.util.DateTimeUtil;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

@Service
@ESBServiceMapping(ESBServiceType.DEPOSIT_CA_ACCT_FLOAT_DETAILS)
public class DepositCAAcctFloatDetailsResponseProcessor implements ESBResponseProcessor<DepositAcctFloatDTO> {
    private static final Logger log = LoggerFactory.getLogger(DepositCAAcctFloatDetailsResponseProcessor.class);
    private final ObjectMapper objectMapper;
    private final ESBDepositCAAcctFloatDetailsMapper eSBDepositCAAcctFloatDetailsMapper;
    private final AsyncPaginationService asyncPaginationService;
    private final AsyncMappingService asyncMappingService;
    private final ESBClient esbClient;
    private final ESBIntegrationProperties esbIntegrationProperties;
    private final ESBMetadataService metadataService;
    private static final String TERMINATING_KEY = "00000000";
    private static final String STOP_KEY = "0";

    @Autowired
    public DepositCAAcctFloatDetailsResponseProcessor(ObjectMapper objectMapper, ESBDepositCAAcctFloatDetailsMapper eSBDepositCAAcctFloatDetailsMapper, AsyncPaginationService asyncPaginationService, AsyncMappingService asyncMappingService, ESBClient esbClient, ESBIntegrationProperties esbIntegrationProperties, ESBMetadataService metadataService) {
        this.objectMapper = objectMapper;
        this.eSBDepositCAAcctFloatDetailsMapper = eSBDepositCAAcctFloatDetailsMapper;
        this.asyncPaginationService = asyncPaginationService;
        this.asyncMappingService = asyncMappingService;
        this.esbClient = esbClient;
        this.esbIntegrationProperties = esbIntegrationProperties;
        this.metadataService = metadataService;
    }

    @Override
    public DepositAcctFloatDTO processResponse(ESBDTO result, Object request) {
        ESBDepositCAFloatReqDTO receivedRequest = objectMapper.convertValue(request, ESBDepositCAFloatReqDTO.class);
        DepositAcctFloatDTO depositAcctFloatDTO = new DepositAcctFloatDTO();
        List<ESBDepositCAAccountFloatDetailTrnRecDTO> allDatas = new ArrayList<>();
        crawling(result, receivedRequest, allDatas);

        if (!allDatas.isEmpty()) {
            CompletableFuture<List<DepositAcctFloatDtlDTO>> futureResult = asyncMappingService.mapToListAsync(allDatas, eSBDepositCAAcctFloatDetailsMapper::mapToDepositAccountFloatDetailResp);
            Function<List<ESBDepositCAAccountFloatDetailTrnRecDTO>, List<DepositAcctFloatDtlDTO>> trxMapper =
                    dtos -> futureResult.join();
            SearchCollectionResponse<DepositAcctFloatDtlDTO> collectionResponse = asyncPaginationService.getPaginatedResponse(allDatas, trxMapper, receivedRequest.getPageSize(), receivedRequest.getPage());
            //process and set details from ESB
            depositAcctFloatDTO.setDepositAcctFloatDetails(collectionResponse);
            depositAcctFloatDTO.setTotalFloat(calculateTotalFloat(allDatas));
        }
        depositAcctFloatDTO.setExpiryDate(setFormattedExpiryDate(receivedRequest.getExpiryDate()));
        depositAcctFloatDTO.setCurrency(receivedRequest.getCurrency());
        // esb metadata injection
        metadataService.attachMetadata(result, depositAcctFloatDTO);
        return depositAcctFloatDTO;
    }

    private void crawling(ESBDTO result, ESBDepositCAFloatReqDTO request, List<ESBDepositCAAccountFloatDetailTrnRecDTO> allDatas) {
        String lastKey = "";
        long startTime = System.currentTimeMillis();
        ESBPropertiesDTO configProperties = esbIntegrationProperties.getIbmMq(PropertyServiceEnum.DEPOSIT_CA_GETFLOATDETAILS.getValue());

        do {
            ESBDepositCAAccountFloatDetailDTO respMsgBody = objectMapper.convertValue(result.getMessage().getMsgBody(), ESBDepositCAAccountFloatDetailDTO.class);
            if (respMsgBody == null) {
                log.error("Response message body conversion failed for ESB Deposit CA Float Details Inquiry");
                break;
            }
            List<ESBDepositCAAccountFloatDetailTrnRecDTO> datas = respMsgBody.getAcctTrnRec();
            if (datas != null) {
                allDatas.addAll(datas);
            }

            lastKey = respMsgBody.getMbbLastKey();

            if (Objects.isNull(lastKey) || lastKey.isEmpty()) {
                log.error("mbbLastKey is null or empty for ESB Deposit CA Float Details Inquiry, breaking out of the loop");
                break;
            } else if (!lastKey.equals(TERMINATING_KEY)
                    && !lastKey.equals(STOP_KEY)
                    && result.getMessage().getMsgHeader().getStatusCode().equals(ESBStatusCodeEnum.SUCCESS.getValue())) {
                if(request.getMbbLastKey() != null){
                    if(request.getMbbLastKey().equalsIgnoreCase(lastKey)){ //make sure to stop call ESB if mbbLastKey is repeated
                        log.error("Previous and Next mbbLastKey is same for ESB Deposit CA Float Details Inquiry --> {}", lastKey);
                        break;
                    }
                }
                ESBReqDTO esbReqDTO = ESBReqDTO.builder()
                        .message(ESBMsgReqDTO.builder()
                                .messageHeader(esbClient.constructMsgHeader(configProperties))
                                .messageBody(ESBDepositCAAcctFloatDetailsReqDTO.builder()
                                        .accountNo(request.getAccountNo())
                                        .floatExpiryDate(convertDateFormat(request.getExpiryDate()))
                                        .mbbLastKey(lastKey) //update new key here
                                        .build())
                                .build())
                        .build();
                result = executeSpecialCase(esbReqDTO);
            }

        } while (!(lastKey.equals(TERMINATING_KEY) || lastKey.equals(STOP_KEY))
                && result.getMessage().getMsgHeader().getStatusCode().equals(ESBStatusCodeEnum.SUCCESS.getValue()));
        long endTime = System.currentTimeMillis();
        log.info("ESB for Deposit CA Float Details Inquiry call duration (special case) : {} ms", (endTime - startTime));
        log.info("Total records fetched from ESB for Deposit CA Float Details Inquiry : {} ", allDatas.size());
    }

    private String calculateTotalFloat(List<ESBDepositCAAccountFloatDetailTrnRecDTO> details) {
        return String.valueOf(details.stream()
                .mapToDouble(detail -> {
                    try {
                        return Float.parseFloat(detail.getFloatAmount());
                    } catch (Exception e) {
                        log.error("Error parsing float amount for {}", detail.getFloatAmount(), e);
                        return 0.0;
                    }
                }).sum());
    }

    private String setFormattedExpiryDate(String floatExpiryDate) {
        try {
            String formattedDate = DateTimeUtil.formatFloatExpiryDate(floatExpiryDate);
            return formattedDate;
        } catch (Exception e) {
            log.error("Error formatting ESB float expiry date for {}", floatExpiryDate, e);
            return null;
        }
    }

    private String convertDateFormat(String expiryDate) {
        try {
            //format the date to hit ESB float
            DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("ddMMyyyy");
            DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
            LocalDate parsedDate = LocalDate.parse(expiryDate, inputFormatter);
            return parsedDate.format(outputFormatter);
        } catch (DateTimeParseException e) {
            return "Invalid date format";
        }
    }

    @SneakyThrows
    public <T> ESBDTO executeSpecialCase(ESBReqDTO request) {
        String esbRequestJson = objectMapper.writeValueAsString(request);
        log.info("executeSpecialCase :: Preparing call for ESB --> {}", esbRequestJson);
        String resp = esbClient.callHost(objectMapper.writeValueAsString(request), PropertyServiceEnum.ESBMY);
        log.info("executeSpecialCase :: Response payload for ESB --> {}", resp);

        if (Objects.nonNull(resp)) {
            ESBDTO result = objectMapper.readValue(resp, ESBDTO.class);
            return result;
        }
        return new ESBDTO();
    }

}
