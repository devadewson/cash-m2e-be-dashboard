package com.m2e.dashboard.service.loan.tlf.jms.account.activityinquiry;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.m2e.dashboard.config.jms.ESBIntegrationProperties;
import com.m2e.dashboard.config.jms.ESBServiceMapping;
import com.m2e.dashboard.dto.common.SearchCollectionResponse;
import com.m2e.dashboard.dto.loan.tlf.request.LNTrxHistoryReqDTO;
import com.m2e.dashboard.dto.loan.tlf.response.LNTLFTrxHistoryDTO;
import com.m2e.dashboard.dto.loan.tlf.response.LNTLFTrxHistoryResponseDTO;
import com.m2e.dashboard.enums.ESBServiceType;
import com.m2e.dashboard.enums.ESBStatusCodeEnum;
import com.m2e.dashboard.enums.PropertyServiceEnum;
import com.m2e.dashboard.jms.common.ESBPropertiesDTO;
import com.m2e.dashboard.jms.common.request.ESBDTRangeReqDTO;
import com.m2e.dashboard.jms.common.request.ESBMsgReqDTO;
import com.m2e.dashboard.jms.common.request.ESBReqDTO;
import com.m2e.dashboard.jms.common.response.ESBDTO;
import com.m2e.dashboard.jms.loan.tlf.request.account.activityinquiry.ESBLoanTLFAcctActivityInquiryMsgBodyReqDTO;
import com.m2e.dashboard.jms.loan.tlf.response.account.activityinquiry.ESBLoanTLFAcctActivityInquiryMsgBodyDTO;
import com.m2e.dashboard.jms.loan.tlf.response.account.activityinquiry.ESBLoanTLFAcctActivityInquiryTrnRecDTO;
import com.m2e.dashboard.mapper.loan.tlf.ESBLoanTLFAcctActivityInquiryMapper;
import com.m2e.dashboard.service.*;
import com.m2e.dashboard.service.esb.client.ESBClient;
import com.m2e.dashboard.service.esb.ESBMetadataService;
import com.m2e.dashboard.service.esb.ESBResponseProcessor;
import lombok.SneakyThrows;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.function.Predicate;

import static com.m2e.dashboard.util.DateTimeUtil.*;

@Service
@ESBServiceMapping(ESBServiceType.LOAN_TLF_ACCT_ACTIVITY_INQUIRY_MY_CONV)
public class LoanTLFAcctActivityInquiryMYConvResponseProcessor implements ESBResponseProcessor<LNTLFTrxHistoryResponseDTO> {

    private static final Logger log = LoggerFactory.getLogger(LoanTLFAcctActivityInquiryMYConvResponseProcessor.class);
    private final ObjectMapper objectMapper;
    private final ESBLoanTLFAcctActivityInquiryMapper esbLoanTLFAcctActivityInquiryMapper;
    private final AsyncPaginationService asyncPaginationService;
    private final AsyncMappingService asyncMappingService;
    private final ESBClient esbClient;
    private final ESBIntegrationProperties esbIntegrationProperties;
    private final ESBMetadataService metadataService;
    private static final String TERMINATING_KEY = "00000000";
    private static final String STOP_KEY = "0";

    public LoanTLFAcctActivityInquiryMYConvResponseProcessor(ObjectMapper objectMapper, ESBLoanTLFAcctActivityInquiryMapper esbLoanTLFAcctActivityInquiryMapper, AsyncPaginationService asyncPaginationService, AsyncMappingService asyncMappingService, ESBClient esbClient, ESBIntegrationProperties esbIntegrationProperties, ESBMetadataService metadataService) {
        this.objectMapper = objectMapper;
        this.esbLoanTLFAcctActivityInquiryMapper = esbLoanTLFAcctActivityInquiryMapper;
        this.asyncPaginationService = asyncPaginationService;
        this.asyncMappingService = asyncMappingService;
        this.esbClient = esbClient;
        this.esbIntegrationProperties = esbIntegrationProperties;
        this.metadataService = metadataService;
    }

    @Override
    public LNTLFTrxHistoryResponseDTO processResponse(ESBDTO result, Object request) {
        LNTrxHistoryReqDTO receivedRequest = objectMapper.convertValue(request, LNTrxHistoryReqDTO.class);
        List<ESBLoanTLFAcctActivityInquiryTrnRecDTO> allDatas = new ArrayList<>();
        LNTLFTrxHistoryResponseDTO loanTlfTrxHistoryResponse = new LNTLFTrxHistoryResponseDTO();
        crawling(result, receivedRequest, allDatas);
        CompletableFuture<List<LNTLFTrxHistoryDTO>> futureResult = asyncMappingService.mapToListAsync(allDatas, esbLoanTLFAcctActivityInquiryMapper::mapToLoanTrxHistoryRes);

        //combine the predicates using OR logic
        Predicate<LNTLFTrxHistoryDTO> combinedFilterCondition = dto ->
                StringUtils.containsIgnoreCase(dto.getTrxReference(), receivedRequest.getSearchParam()) ||
                        StringUtils.containsIgnoreCase(dto.getTrxDesc(), receivedRequest.getSearchParam()) ||
                        StringUtils.containsIgnoreCase(dto.getCurrency(), receivedRequest.getSearchParam());

        List<Function<List<LNTLFTrxHistoryDTO>, List<LNTLFTrxHistoryDTO>>> sortFunctions = new ArrayList<>();
        sortFunctions.add(list -> list.stream()
                .filter(dto -> dto.getDate() != null)
                .sorted((a, b) -> b.getDate().compareTo(a.getDate()))
                .toList());

        CompletableFuture<List<LNTLFTrxHistoryDTO>> filteredResult = futureResult.thenCompose(mappedList ->
                asyncMappingService.processListAsync(mappedList,
                        dto -> dto,
                        Collections.singletonList(combinedFilterCondition),
                        sortFunctions)
        );

        List<LNTLFTrxHistoryDTO> finalFilteredList = filteredResult.join();
        int totalFilteredRecords = finalFilteredList.size();
        Function<List<ESBLoanTLFAcctActivityInquiryTrnRecDTO>, List<LNTLFTrxHistoryDTO>> trxMapper =
                dtos -> finalFilteredList;

        SearchCollectionResponse<LNTLFTrxHistoryDTO> esbTLFTrxHists = asyncPaginationService.getPaginatedResponse(allDatas, trxMapper, receivedRequest.getPageSize(), receivedRequest.getPage());
        esbTLFTrxHists.setTotalRecordCount(totalFilteredRecords);
        log.info("ESB Term Loan filtered trx history result --> {}", esbTLFTrxHists);
        // esb metadata injection
        metadataService.attachMetadata(result, loanTlfTrxHistoryResponse);
        loanTlfTrxHistoryResponse.setDatas(esbTLFTrxHists);
        return loanTlfTrxHistoryResponse;
    }

    private void crawling(ESBDTO result, LNTrxHistoryReqDTO request, List<ESBLoanTLFAcctActivityInquiryTrnRecDTO> allDatas) {
        String lastKey = "";
        long startTime = System.currentTimeMillis();
        ESBPropertiesDTO configProperties = esbIntegrationProperties.getIbmMq(PropertyServiceEnum.LOAN_TLF_GETACCTACTIVITYINQUIRY_MY_CONV.getValue());

        if(result.getMessage().getMsgBody() != null) {
            do {
                ESBLoanTLFAcctActivityInquiryMsgBodyDTO respMsgBody = objectMapper.convertValue(result.getMessage().getMsgBody(), ESBLoanTLFAcctActivityInquiryMsgBodyDTO.class);
                if (respMsgBody != null) {
                    List<ESBLoanTLFAcctActivityInquiryTrnRecDTO> datas = respMsgBody.getAcctTrnRec();
                    if (datas != null) {
                        allDatas.addAll(datas);
                    }

                    lastKey = respMsgBody.getMbbLastKey();

                    if (Objects.isNull(lastKey) || lastKey.isEmpty()) {
                        log.error("mbbLastKey is null or empty for ESB Loan TL MY Activity Inquiry, breaking out of the loop");
                        break;
                    } else if (!TERMINATING_KEY.equals(lastKey)
                            && !STOP_KEY.equals(lastKey)
                            && result.getMessage().getMsgHeader().getStatusCode().equals(ESBStatusCodeEnum.SUCCESS.getValue())) {
                        if (request.getMbbLastKey() != null) {
                            if (request.getMbbLastKey().equalsIgnoreCase(lastKey)) { //make sure to stop call ESB if mbbLastKey is repeated
                                log.error("Previous and Next mbbLastKey is same --> {}", lastKey);
                                break;
                            }
                        }
                        request.setMbbLastKey(lastKey); //update new key here
                        ESBReqDTO esbReqDTO = ESBReqDTO.builder()
                                .message(ESBMsgReqDTO.builder()
                                        .messageHeader(esbClient.constructMsgHeader(configProperties))
                                        .messageBody(createESBAcctActivityInquiryReqDTO(request)).build())
                                .build();
                        result = executeSpecialCase(esbReqDTO);
                    }
                }

            } while (!(TERMINATING_KEY.equals(lastKey) || STOP_KEY.equals(lastKey))
                    && ESBStatusCodeEnum.SUCCESS.getValue().equals(result.getMessage().getMsgHeader().getStatusCode()));
        }
        long endTime = System.currentTimeMillis();
        log.info("ESB for Loan Term Loan Activity Inquiry (MY Conventional) call duration (special case) : {} ms", (endTime - startTime));
        log.info("Total records fetched from ESB for Loan Term Loan Activity Inquiry : {} ", allDatas.size());
    }

    public ESBLoanTLFAcctActivityInquiryMsgBodyReqDTO createESBAcctActivityInquiryReqDTO(LNTrxHistoryReqDTO request) {
        return ESBLoanTLFAcctActivityInquiryMsgBodyReqDTO.builder()
                .accountNumber(request.getAcctNumber())
                .accountType("L")
                .currency(request.getCurrency())
                .mbbLastKey(request.getMbbLastKey())
                .dtRange(ESBDTRangeReqDTO.builder()
                    .startDate(Objects.nonNull(request.getFilters()) && (org.springframework.util.StringUtils.hasText(request.getFilters().getStartDate()))
                            ? getESBDate(request.getFilters().getStartDate())
                            : getESBDefaultDate().get("START_DT"))
                    .endDate(Objects.nonNull(request.getFilters()) && (org.springframework.util.StringUtils.hasText(request.getFilters().getEndDate()))
                            ? getESBDate(request.getFilters().getEndDate())
                            : getESBDefaultDate().get("END_DT"))
                    .startTime(Objects.nonNull(request.getFilters()) && (org.springframework.util.StringUtils.hasText(request.getFilters().getStartTime()))
                            ? getESBTime(request.getFilters().getStartTime())
                            : getESBDefaultTime().get("START_TM"))
                    .endTime(Objects.nonNull(request.getFilters()) && (org.springframework.util.StringUtils.hasText(request.getFilters().getEndTime()))
                            ? getESBTime(request.getFilters().getEndTime())
                            : getESBDefaultTime().get("END_TM"))
                    .build())
                .build();
    }

    @SneakyThrows
    public <T> ESBDTO executeSpecialCase(ESBReqDTO request) {
        log.info("executeSpecialCase :: Preparing call for ESB --> {}", request);
        String resp = esbClient.callHost(objectMapper.writeValueAsString(request), PropertyServiceEnum.ESBMY);
        log.info("executeSpecialCase :: Response payload for ESB --> {}", resp);

        if (Objects.nonNull(resp)) {
            return objectMapper.readValue(resp, ESBDTO.class);
        }
        return new ESBDTO();
    }
}
