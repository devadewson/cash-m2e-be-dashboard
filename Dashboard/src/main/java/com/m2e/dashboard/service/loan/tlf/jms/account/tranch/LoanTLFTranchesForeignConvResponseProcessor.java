package com.m2e.dashboard.service.loan.tlf.jms.account.tranch;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.m2e.dashboard.config.jms.ESBIntegrationProperties;
import com.m2e.dashboard.config.jms.ESBServiceMapping;
import com.m2e.dashboard.dto.common.SearchCollectionResponse;
import com.m2e.dashboard.dto.loan.tlf.response.LNTLFTrancheDTO;
import com.m2e.dashboard.dto.loan.tlf.response.LNTLFTrancheListDTO;
import com.m2e.dashboard.enums.ESBServiceType;
import com.m2e.dashboard.enums.ESBStatusCodeEnum;
import com.m2e.dashboard.enums.PropertyServiceEnum;
import com.m2e.dashboard.jms.common.ESBPropertiesDTO;
import com.m2e.dashboard.jms.common.request.ESBMsgReqDTO;
import com.m2e.dashboard.jms.common.request.ESBReqDTO;
import com.m2e.dashboard.jms.common.response.ESBDTO;
import com.m2e.dashboard.jms.common.response.ESBMsgDTO;
import com.m2e.dashboard.jms.loan.tlf.request.tranch.details.ESBLoanTLFTranchesDetailsReqDTO;
import com.m2e.dashboard.jms.loan.tlf.request.tranch.details.ESBLoanTLFTranchesReqDTO;
import com.m2e.dashboard.jms.loan.tlf.response.tranch.details.ESBLoanTLFTranchesMsgBodyDTO;
import com.m2e.dashboard.jms.loan.tlf.response.tranch.details.ESBLoanTLFTranchesTrnRecDTO;
import com.m2e.dashboard.mapper.loan.tlf.ESBLoanTLFTranchesMapper;
import com.m2e.dashboard.service.*;
import com.m2e.dashboard.service.esb.client.ESBClient;
import com.m2e.dashboard.service.esb.ESBMetadataService;
import com.m2e.dashboard.service.esb.ESBResponseProcessor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.function.Predicate;

@Service
@Slf4j
@ESBServiceMapping(ESBServiceType.LOAN_TLF_TRANCHES_INQUIRY_FOREIGN_CONV)
public class LoanTLFTranchesForeignConvResponseProcessor implements ESBResponseProcessor<LNTLFTrancheListDTO> {
    private final ObjectMapper objectMapper;
    private final ESBLoanTLFTranchesMapper esbLoanTLFTranchesMapper;
    private final AsyncPaginationService asyncPaginationService;
    private final AsyncMappingService asyncMappingService;
    private final ESBClient esbClient;
    private final ESBIntegrationProperties esbIntegrationProperties;
    private final ESBMetadataService metadataService;
    private static final String TERMINATING_KEY = "00000000";
    private static final String STOP_KEY = "0";

    @Autowired
    public LoanTLFTranchesForeignConvResponseProcessor(ObjectMapper objectMapper, ESBLoanTLFTranchesMapper esbLoanTLFTranchesMapper, AsyncPaginationService asyncPaginationService, AsyncMappingService asyncMappingService, ESBClient esbClient, ESBIntegrationProperties esbIntegrationProperties, ESBMetadataService metadataService) {
        this.objectMapper = objectMapper;
        this.esbLoanTLFTranchesMapper = esbLoanTLFTranchesMapper;
        this.asyncPaginationService = asyncPaginationService;
        this.asyncMappingService = asyncMappingService;
        this.esbClient = esbClient;
        this.esbIntegrationProperties = esbIntegrationProperties;
        this.metadataService = metadataService;
    }

    @Override
    public LNTLFTrancheListDTO processResponse(ESBDTO result, Object request) {
        ESBLoanTLFTranchesReqDTO receivedRequest = objectMapper.convertValue(request, ESBLoanTLFTranchesReqDTO.class);
        List<ESBLoanTLFTranchesTrnRecDTO> allDatas = new ArrayList<>();
        LNTLFTrancheListDTO lntlfTrancheListResponse = new LNTLFTrancheListDTO();
        crawling(result, receivedRequest, allDatas);

        String accountCurrency = getEsbDataCurrency(result);
        if (accountCurrency==null || accountCurrency.isEmpty()) {
            log.error("Invalid ESB currency");
            return lntlfTrancheListResponse;
        }
        CompletableFuture<List<LNTLFTrancheDTO>> futureResult = asyncMappingService.mapToListAsync(allDatas, esbData -> esbLoanTLFTranchesMapper.mapToLoanTranchesRes(esbData, accountCurrency));

        //combine the predicates using OR logic
        Predicate<LNTLFTrancheDTO> combinedFilterCondition = dto ->
                StringUtils.containsIgnoreCase(dto.getTrancheNumber(), receivedRequest.getSearchParam()) ||
                        StringUtils.containsIgnoreCase(dto.getCurrency(), receivedRequest.getSearchParam());

        List<Function<List<LNTLFTrancheDTO>, List<LNTLFTrancheDTO>>> sortFunctions = new ArrayList<>();
        sortFunctions.add(list -> list.stream()
                .filter(dto -> dto.getTrancheNumber() != null)
                .sorted((a, b) -> b.getCurrency().compareTo(a.getCurrency()))
                .toList());

        CompletableFuture<List<LNTLFTrancheDTO>> filteredResult = futureResult.thenCompose(mappedList ->
                asyncMappingService.processListAsync(mappedList,
                        dto -> dto,
                        Collections.singletonList(combinedFilterCondition),
                        sortFunctions)
        );

        List<LNTLFTrancheDTO> finalFilteredList = filteredResult.join();
        int totalFilteredRecords = finalFilteredList.size();
        Function<List<ESBLoanTLFTranchesTrnRecDTO>, List<LNTLFTrancheDTO>> trxMapper =
                dtos -> finalFilteredList;

        SearchCollectionResponse<LNTLFTrancheDTO> esbTLFTranches = asyncPaginationService.getPaginatedResponse(allDatas, trxMapper, receivedRequest.getPageSize(), receivedRequest.getPage());
        esbTLFTranches.getData().forEach(tranch -> tranch.setCurrency(receivedRequest.getAccountCurrency()));
        esbTLFTranches.setTotalRecordCount(totalFilteredRecords);
        log.info("ESB Term Loan filtered tranches result --> {}", esbTLFTranches);
        // esb metadata injection
        metadataService.attachMetadata(result, lntlfTrancheListResponse);
        lntlfTrancheListResponse.setDatas(esbTLFTranches);
        return lntlfTrancheListResponse;
    }

    private String getEsbDataCurrency(ESBDTO result) {
        ESBMsgDTO<ESBLoanTLFTranchesMsgBodyDTO> respMsgBody = objectMapper.convertValue(result.getMessage(), new TypeReference<>() {});
        return respMsgBody.getMsgBody()==null ? null : respMsgBody.getMsgBody().getCurrency();
    }


    private void crawling(ESBDTO result, ESBLoanTLFTranchesReqDTO request, List<ESBLoanTLFTranchesTrnRecDTO> allDatas) {
        String lastKey = "";
        long startTime = System.currentTimeMillis();
        ESBPropertiesDTO configProperties = esbIntegrationProperties.getIbmMq(PropertyServiceEnum.LOAN_TLF_GETTRANCHDETAILS_FOREIGN_CONV.getValue());

        if(result.getMessage().getMsgBody() != null) {
            do {
                ESBLoanTLFTranchesMsgBodyDTO respMsgBody = objectMapper.convertValue(result.getMessage().getMsgBody(), ESBLoanTLFTranchesMsgBodyDTO.class);
                if (respMsgBody == null) {
                    log.error("Response message body conversion failed for ESB Loan TLF Activity Inquiry");
                    break;
                }

                List<ESBLoanTLFTranchesTrnRecDTO> datas = respMsgBody.getTranchDetails();
                if (datas != null) {
                    allDatas.addAll(datas);
                }
                lastKey = respMsgBody.getMbbLastKey();

                if (Objects.isNull(lastKey) || lastKey.isEmpty()) {
                    log.error("mbbLastKey is null or empty for ESB Loan TL Tranches Inquiry, breaking out of the loop");
                    break;
                } else if (!lastKey.equals(TERMINATING_KEY)
                        && !lastKey.equals(STOP_KEY)
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
                                    .messageBody(createESBTranchesInquiryReqDTO(request)).build())
                            .build();
                    result = executeSpecialCase(esbReqDTO);
                }
            } while (!(lastKey.equals(TERMINATING_KEY) || lastKey.equals(STOP_KEY))
                    && result.getMessage().getMsgHeader().getStatusCode().equals(ESBStatusCodeEnum.SUCCESS.getValue()));
        }
        long endTime = System.currentTimeMillis();
        log.info("ESB for Loan TL Tranches Inquiry call duration (special case) : {} ms", (endTime - startTime));
        log.info("Total records fetched from ESB for Loan TL Tranches Inquiry : {} ", allDatas.size());
    }

    public ESBLoanTLFTranchesDetailsReqDTO createESBTranchesInquiryReqDTO(ESBLoanTLFTranchesReqDTO request) {
        return ESBLoanTLFTranchesDetailsReqDTO.builder()
                .accountNo(request.getAcctNumber())
                .accountType("L")
                .currency(request.getAccountCurrency())
                .mbbLastKey(request.getMbbLastKey())
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