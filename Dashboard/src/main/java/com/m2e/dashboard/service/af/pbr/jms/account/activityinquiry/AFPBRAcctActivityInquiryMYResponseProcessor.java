package com.m2e.dashboard.service.af.pbr.jms.account.activityinquiry;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.m2e.dashboard.config.jms.ESBIntegrationProperties;
import com.m2e.dashboard.config.jms.ESBServiceMapping;
import com.m2e.dashboard.dto.af.response.pbr.AFPBRTrxHistoryDTO;
import com.m2e.dashboard.dto.af.response.pbr.AFPBRTrxHistorySummaryDTO;
import com.m2e.dashboard.dto.common.SearchCollectionResponse;
import com.m2e.dashboard.enums.ESBServiceType;
import com.m2e.dashboard.enums.ESBStatusCodeEnum;
import com.m2e.dashboard.enums.PropertyServiceEnum;
import com.m2e.dashboard.jms.af.common.ESBAFAcctActivityReqDTO;
import com.m2e.dashboard.jms.af.pbr.request.account.activityinquiry.ESBAFPBRAcctActivityInquiryMsgBodyReqDTO;
import com.m2e.dashboard.jms.af.pbr.response.account.activityinquiry.ESBAFPBRAcctActivityInquiryMsgBodyDTO;
import com.m2e.dashboard.jms.af.pbr.response.account.activityinquiry.ESBAFPBRAcctActivityInquiryTrnRecDTO;
import com.m2e.dashboard.jms.common.ESBPropertiesDTO;
import com.m2e.dashboard.jms.common.request.ESBMsgReqDTO;
import com.m2e.dashboard.jms.common.request.ESBReqDTO;
import com.m2e.dashboard.jms.common.response.ESBDTO;
import com.m2e.dashboard.mapper.af.pbr.ESBAFPBRAcctActivityInquiryMapper;
import com.m2e.dashboard.service.*;
import com.m2e.dashboard.service.esb.client.ESBClient;
import com.m2e.dashboard.service.esb.ESBMetadataService;
import com.m2e.dashboard.service.esb.ESBResponseProcessor;
import com.m2e.dashboard.util.DateTimeUtil;
import lombok.SneakyThrows;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;


@Service
@ESBServiceMapping(ESBServiceType.AF_PBR_ACCT_ACTIVITY_INQUIRY_MY)
public class AFPBRAcctActivityInquiryMYResponseProcessor implements ESBResponseProcessor<AFPBRTrxHistorySummaryDTO> {

    private static final Logger log = LoggerFactory.getLogger(AFPBRAcctActivityInquiryMYResponseProcessor.class);
    private final ObjectMapper objectMapper;
    private final ESBAFPBRAcctActivityInquiryMapper esbafpbrAcctActivityInquiryMapper;
    private final AsyncPaginationService asyncPaginationService;
    private final AsyncMappingService asyncMappingService;
    private final ESBClient esbClient;
    private final ESBIntegrationProperties esbIntegrationProperties;
    private final ESBMetadataService metadataService;
    private static final String TERMINATING_KEY = "00000000";
    private static final String STOP_KEY = "0";

    @Autowired
    public AFPBRAcctActivityInquiryMYResponseProcessor(ObjectMapper objectMapper, ESBAFPBRAcctActivityInquiryMapper esbafpbrAcctActivityInquiryMapper, 
                                                       AsyncPaginationService asyncPaginationService, AsyncMappingService asyncMappingService, 
                                                       ESBClient esbClient, ESBIntegrationProperties esbIntegrationProperties,
                                                       ESBMetadataService metadataService) {
        this.objectMapper = objectMapper;
        this.esbafpbrAcctActivityInquiryMapper = esbafpbrAcctActivityInquiryMapper;
        this.asyncPaginationService = asyncPaginationService;
        this.asyncMappingService = asyncMappingService;
        this.esbClient = esbClient;
        this.esbIntegrationProperties = esbIntegrationProperties;
        this.metadataService = metadataService;
    }

    @Override
    public AFPBRTrxHistorySummaryDTO processResponse(ESBDTO result, Object request) {
        ESBAFAcctActivityReqDTO receivedRequest = objectMapper.convertValue(request, ESBAFAcctActivityReqDTO.class);
        try {
            log.info("receivedRequest result --> {}", objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(receivedRequest));
        } catch (Exception e) {
            log.info("error objectmapper -> {}", e);
        }
        List<ESBAFPBRAcctActivityInquiryTrnRecDTO> allDatas = new ArrayList<>();
        AFPBRTrxHistorySummaryDTO afpbrTrxHistoryDTO = new AFPBRTrxHistorySummaryDTO();
        crawling(result, receivedRequest, allDatas);
        CompletableFuture<List<AFPBRTrxHistoryDTO>> futureResult = asyncMappingService.mapToListAsync(allDatas, esbafpbrAcctActivityInquiryMapper::mapToTrxHistoryRes);

        List<Predicate<AFPBRTrxHistoryDTO>> filterConditions = constructFilter(receivedRequest);

        List<Function<List<AFPBRTrxHistoryDTO>, List<AFPBRTrxHistoryDTO>>> sortFunctions = new ArrayList<>();
        sortFunctions.add(list -> list.stream()
                .sorted((a, b) -> b.getTrxDate().compareTo(a.getTrxDate()))
                .collect(Collectors.toList()));

        CompletableFuture<List<AFPBRTrxHistoryDTO>> filteredResult = futureResult.thenCompose(mappedList ->
                asyncMappingService.processListAsyncAndFilter(mappedList,
                        dto -> dto,
                        filterConditions,
                        sortFunctions)
        );

        List<AFPBRTrxHistoryDTO> finalFilteredList = filteredResult.join();
        int totalFilteredRecords = finalFilteredList.size();
        Function<List<ESBAFPBRAcctActivityInquiryTrnRecDTO>, List<AFPBRTrxHistoryDTO>> trxMapper =
                dtos -> finalFilteredList;

        SearchCollectionResponse<AFPBRTrxHistoryDTO> esbTrxHists = asyncPaginationService.getPaginatedResponse(allDatas, trxMapper, receivedRequest.getPageSize(), receivedRequest.getPage());
        List<AFPBRTrxHistoryDTO> afpbrTrxHistoryDTOList = esbTrxHists.getData();
        //format the date from ESB resp
        afpbrTrxHistoryDTOList.forEach(dto -> {
            try {
                dto.setTrxDate(DateTimeUtil.convertDepositCATrxHistDate(dto.getTrxDate(), dto.getProcessingTime()));
            } catch (Exception e) {
                log.error("Error formatting ESB AF PBR MY Activity Inquiry Date value for {}", dto.getTrxDate());
            }
        });
        esbTrxHists.setTotalRecordCount(totalFilteredRecords);
        try {
            log.info("esbTrxHists result --> {}", objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(esbTrxHists));
        } catch (Exception e) {
            log.info("error objectmapper -> {}", e);
        }
        // esb metadata injection
        metadataService.attachMetadata(result, afpbrTrxHistoryDTO);
        afpbrTrxHistoryDTO.setDatas(esbTrxHists);
        return afpbrTrxHistoryDTO;
    }

    private List<Predicate<AFPBRTrxHistoryDTO>> constructFilter(ESBAFAcctActivityReqDTO receivedRequest) {
        List<Predicate<AFPBRTrxHistoryDTO>> filterConditions = new ArrayList<>();
        Predicate<AFPBRTrxHistoryDTO> searchParamFilter = dto ->
                StringUtils.containsIgnoreCase(dto.getTrxDesc(), receivedRequest.getSearchParam()) ||
                        StringUtils.containsIgnoreCase(dto.getTrxReference(), receivedRequest.getSearchParam()) ||
                        StringUtils.containsIgnoreCase(dto.getCurrency(), receivedRequest.getSearchParam());

        filterConditions.add(searchParamFilter);

        if (StringUtils.isNotEmpty(receivedRequest.getFilters().getTrxCode())) {
            log.info("Filtering by trxCode: {}", receivedRequest.getFilters().getTrxCode());
            filterConditions.add(dto -> {
                String dtoTrxCode = StringUtils.trimToEmpty(dto.getTrxCode());
                String requestTrxCode = StringUtils.trimToEmpty(receivedRequest.getFilters().getTrxCode());

                boolean matches = StringUtils.equalsIgnoreCase(dtoTrxCode, requestTrxCode);
                log.info("Checking trxCode: dtoTrxCode='{}' requestTrxCode='{}' match={}", dtoTrxCode, requestTrxCode, matches);
                return matches;
            });
        }

        if (StringUtils.isNotBlank(receivedRequest.getFilters().getStartDate()) &&
                StringUtils.isNotBlank(receivedRequest.getFilters().getEndDate())) {

            DateTimeFormatter esbDateFormat = DateTimeFormatter.ofPattern("yyyyMMdd");
            DateTimeFormatter requestDateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");

            try {
                LocalDate startDate = LocalDate.parse(receivedRequest.getFilters().getStartDate(), requestDateFormat);
                LocalDate endDate = LocalDate.parse(receivedRequest.getFilters().getEndDate(), requestDateFormat);

                if (startDate.isAfter(endDate)) {
                    throw new IllegalArgumentException("Start date cannot be after end date.");
                }

                filterConditions.add(dto -> {
                    if (StringUtils.isBlank(dto.getTrxDate())) return false;
                    try {
                        LocalDate trxDate = LocalDate.parse(dto.getTrxDate(), esbDateFormat);
                        return !trxDate.isBefore(startDate) && !trxDate.isAfter(endDate);
                    } catch (DateTimeParseException e) {
                        log.warn("Invalid date format in ESB data: {}", dto.getTrxDate());
                        return false;
                    }
                });
            } catch (DateTimeParseException e) {
                log.error("Invalid date format in request: startDate={}, endDate={}",
                        receivedRequest.getFilters().getStartDate(), receivedRequest.getFilters().getEndDate());
            }
        }

        if (StringUtils.isNotBlank(receivedRequest.getFilters().getStartTime()) &&
                StringUtils.isNotBlank(receivedRequest.getFilters().getEndTime())) {

            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

            try {
                LocalTime startTime = LocalTime.parse(receivedRequest.getFilters().getStartTime(), timeFormatter);
                LocalTime endTime = LocalTime.parse(receivedRequest.getFilters().getEndTime(), timeFormatter);

                filterConditions.add(dto -> {
                    if (StringUtils.isBlank(dto.getProcessingTime())) return false;
                    try {
                        // Ensure processingTime is always 6 digits by prepending zeros
                        String processingTime = StringUtils.leftPad(dto.getProcessingTime(), 6, '0');

                        LocalTime trxTime = LocalTime.parse(processingTime, DateTimeFormatter.ofPattern("HHmmss"));
                        return !trxTime.isBefore(startTime) && !trxTime.isAfter(endTime);
                    } catch (DateTimeParseException e) {
                        log.warn("Invalid time format in ESB data: {}", dto.getProcessingTime());
                        return false;
                    }
                });
            } catch (DateTimeParseException e) {
                log.error("Invalid time format in request: startTime={}, endTime={}",
                        receivedRequest.getFilters().getStartTime(), receivedRequest.getFilters().getEndTime());
            }
        }

        if (StringUtils.isNotEmpty(receivedRequest.getFilters().getAmtFrom()) &&
                StringUtils.isNotEmpty(receivedRequest.getFilters().getAmtTo())) {
            log.info("executing amount filter : {} - {}", receivedRequest.getFilters().getAmtFrom(), receivedRequest.getFilters().getAmtTo());
            double amtFrom = Double.parseDouble(receivedRequest.getFilters().getAmtFrom().replace(",", ""));
            log.info("amtFrom value : {}", amtFrom);
            double amtTo = Double.parseDouble(receivedRequest.getFilters().getAmtTo().replace(",", ""));
            log.info("amtTo value : {}", amtTo);

            filterConditions.add(dto -> {
                // Parse credit and debit safely
                Double credit = StringUtils.isNotEmpty(dto.getCredit()) ? Double.parseDouble(dto.getCredit().replace(",", "")) : null;
                log.info("credit value : {}", credit);
                Double debit = StringUtils.isNotEmpty(dto.getDebit()) ? Double.parseDouble(dto.getDebit().replace(",", "")) : null;
                log.info("debit value : {}", debit);
                // Compare values based on availability
                if (credit != null && debit != null) {
                    log.info("executing credit != null && debit != null");
                    return (credit >= amtFrom && credit <= amtTo) || (debit >= amtFrom && debit <= amtTo);
                } else if (credit != null) {
                    log.info("executing credit != null");
                    return credit >= amtFrom && credit <= amtTo;
                } else if (debit != null) {
                    log.info("executing debit != null");
                    return debit >= amtFrom && debit <= amtTo;
                } else {
                    log.info("amount return false");
                    return false;
                }
            });
        }

        return filterConditions;
    }

    private void crawling(ESBDTO result, ESBAFAcctActivityReqDTO request, List<ESBAFPBRAcctActivityInquiryTrnRecDTO> allDatas) {
        String lastKey = "";
        long startTime = System.currentTimeMillis();
        ESBPropertiesDTO configProperties = esbIntegrationProperties.getIbmMq(PropertyServiceEnum.AF_PBR_GETACCTACTIVITYINQUIRY_MY.getValue());

        if(result.getMessage().getMsgBody() != null) {
            do {
                ESBAFPBRAcctActivityInquiryMsgBodyDTO respMsgBody = objectMapper.convertValue(result.getMessage().getMsgBody(), ESBAFPBRAcctActivityInquiryMsgBodyDTO.class);
                if (respMsgBody == null) {
                    log.error("Response message body conversion failed for ESB AF PBR MY Activity Inquiry");
                    break;
                }
                List<ESBAFPBRAcctActivityInquiryTrnRecDTO> datas = respMsgBody.getAcctTrnRec();
                if (datas != null) {
                    allDatas.addAll(datas);
                }

                lastKey = respMsgBody.getMbbLastKey();

                if (Objects.isNull(lastKey) || lastKey.isEmpty()) {
                    log.error("mbbLastKey is null or empty for ESB AF PBR MY Activity Inquiry, breaking out of the loop");
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
                                    .messageBody(createESBAcctBalanceInquiryReqDTO(request)).build())
                            .build();
                    result = executeSpecialCase(esbReqDTO);
                }

            } while (!(lastKey.equals(TERMINATING_KEY) || lastKey.equals(STOP_KEY))
                    && result.getMessage().getMsgHeader().getStatusCode().equals(ESBStatusCodeEnum.SUCCESS.getValue()));
        }
        long endTime = System.currentTimeMillis();
        log.info("ESB for AF PBR Activity Inquiry call duration (special case) : {} ms", (endTime - startTime));
        log.info("Total records fetched from ESB for AF PBR MY Activity Inquiry : {} ", allDatas.size());
    }

    public ESBAFPBRAcctActivityInquiryMsgBodyReqDTO createESBAcctBalanceInquiryReqDTO(ESBAFAcctActivityReqDTO request) {
        return ESBAFPBRAcctActivityInquiryMsgBodyReqDTO.builder()
                .accountNumber(request.getAcctNumber())
                .accountType(request.getAcctType())
                .currency(request.getCurrency())
                .mbbLastKey(request.getMbbLastKey()).build();
    }

    @SneakyThrows
    public ESBDTO executeSpecialCase(ESBReqDTO request) {
        String esbRequestJson = objectMapper.writeValueAsString(request);
        log.info("executeSpecialCase :: Preparing call for ESB --> {}", esbRequestJson);
        String resp = esbClient.callHost(esbRequestJson, PropertyServiceEnum.ESBMY);
        log.info("executeSpecialCase :: Response payload for ESB --> {}", resp);

        if (Objects.nonNull(resp)) {
            return objectMapper.readValue(resp, ESBDTO.class);
        }
        return new ESBDTO();
    }

}
