//package com.m2e.dashboard.service.deposit.va.jms.account.activityinquiry.my;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.m2e.dashboard.config.jms.ESBIntegrationProperties;
//import com.m2e.dashboard.config.jms.ESBServiceMapping;
//import com.m2e.dashboard.dto.common.SearchCollectionResponse;
//import com.m2e.dashboard.dto.deposit.va.response.DepositVATransHistSearchDTO;
//import com.m2e.dashboard.dto.deposit.va.response.DepositVATrxHistoryDTO;
//import com.m2e.dashboard.enums.ESBServiceType;
//import com.m2e.dashboard.enums.ESBStatusCodeEnum;
//import com.m2e.dashboard.enums.PropertyServiceEnum;
//import com.m2e.dashboard.jms.common.ESBPropertiesDTO;
//import com.m2e.dashboard.jms.common.request.ESBDTRangeReqDTO;
//import com.m2e.dashboard.jms.common.request.ESBMsgReqDTO;
//import com.m2e.dashboard.jms.common.request.ESBReqDTO;
//import com.m2e.dashboard.jms.common.response.ESBDTO;
//import com.m2e.dashboard.jms.deposit.ca.request.account.activityinquiry.ESBDepositCAAcctActivityInquiryMsgBodyReqDTO;
//import com.m2e.dashboard.jms.deposit.ca.request.account.activityinquiry.ESBDepositCAAcctActivityReqDTO;
//import com.m2e.dashboard.jms.deposit.ca.response.account.activityinquiry.ESBDepositCAAcctActivityInquiryMsgBodyDTO;
//import com.m2e.dashboard.jms.deposit.ca.response.account.activityinquiry.ESBDepositCAAcctActivityInquiryTrnRecDTO;
//import com.m2e.dashboard.mapper.deposit.va.ESBDepositVAAcctActivityInquiryMapper;
//import com.m2e.dashboard.service.*;
//import com.m2e.dashboard.service.esb.client.ESBClient;
//import com.m2e.dashboard.service.esb.ESBMetadataService;
//import com.m2e.dashboard.service.esb.ESBResponseProcessor;
//import com.m2e.dashboard.util.DateTimeUtil;
//import lombok.SneakyThrows;
//import org.apache.commons.lang.StringUtils;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.time.LocalDate;
//import java.time.LocalTime;
//import java.time.format.DateTimeFormatter;
//import java.time.format.DateTimeParseException;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Objects;
//import java.util.concurrent.CompletableFuture;
//import java.util.function.Function;
//import java.util.function.Predicate;
//import java.util.stream.Collectors;
//
//import static com.m2e.dashboard.util.DateTimeUtil.*;
//
//@Service
//@ESBServiceMapping(ESBServiceType.DEPOSIT_VA_ACCT_ACTIVITY_INQUIRY_MY)
//public class DepositVAAcctActivityInquiryMYResponseProcessor implements ESBResponseProcessor<DepositVATrxHistoryDTO> {
//    private static final Logger log = LoggerFactory.getLogger(DepositVAAcctActivityInquiryMYResponseProcessor.class);
//    private final ObjectMapper objectMapper;
//    private final ESBDepositVAAcctActivityInquiryMapper esbDepositVAAcctActivityInquiryMapper;
//    private final AsyncPaginationService asyncPaginationService;
//    private final AsyncMappingService asyncMappingService;
//    private final ESBClient esbClient;
//    private final ESBIntegrationProperties esbIntegrationProperties;
//    private final ESBMetadataService metadataService;
//    private static final String TERMINATING_KEY = "00000000";
//    private static final String STOP_KEY = "0";
//
//    @Autowired
//    public DepositVAAcctActivityInquiryMYResponseProcessor(ObjectMapper objectMapper, ESBDepositVAAcctActivityInquiryMapper esbDepositVAAcctActivityInquiryMapper, AsyncPaginationService asyncPaginationService, AsyncMappingService asyncMappingService, ESBClient esbClient, ESBIntegrationProperties esbIntegrationProperties, ESBMetadataService metadataService) {
//        this.objectMapper = objectMapper;
//        this.esbDepositVAAcctActivityInquiryMapper = esbDepositVAAcctActivityInquiryMapper;
//        this.asyncPaginationService = asyncPaginationService;
//        this.asyncMappingService = asyncMappingService;
//        this.esbClient = esbClient;
//        this.esbIntegrationProperties = esbIntegrationProperties;
//        this.metadataService = metadataService;
//    }
//
//    @Override
//    public DepositVATrxHistoryDTO processResponse(ESBDTO result, Object request) {
//        ESBDepositCAAcctActivityReqDTO receivedRequest = objectMapper.convertValue(request, ESBDepositCAAcctActivityReqDTO.class);
//        try{
//            log.info("receivedRequest result --> {}", objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(receivedRequest));
//        } catch (Exception e){
//            log.info("error objectmapper -> {}", e);
//        }
//        List<ESBDepositCAAcctActivityInquiryTrnRecDTO> allDatas = new ArrayList<>();
//        DepositVATrxHistoryDTO depositVATrxHistoryDTO = new DepositVATrxHistoryDTO();
//        crawling(result, receivedRequest, allDatas);
//        CompletableFuture<List<DepositVATransHistSearchDTO>> futureResult = asyncMappingService.mapToListAsync(allDatas, esbDepositVAAcctActivityInquiryMapper::mapToDepositTrxHistoryRes);
//
//        List<Predicate<DepositVATransHistSearchDTO>> filterConditions = constructFilter(receivedRequest);
//
//        List<Function<List<DepositVATransHistSearchDTO>, List<DepositVATransHistSearchDTO>>> sortFunctions = new ArrayList<>();
//        sortFunctions.add(list -> list.stream()
//                .sorted((a, b) -> b.getDate().compareTo(a.getDate()))
//                .collect(Collectors.toList()));
//
//        CompletableFuture<List<DepositVATransHistSearchDTO>> filteredResult = futureResult.thenCompose(mappedList ->
//                asyncMappingService.processListAsyncAndFilter(mappedList,
//                        dto -> dto,
//                        filterConditions,
//                        sortFunctions)
//        );
//
//        List<DepositVATransHistSearchDTO> finalFilteredList = filteredResult.join();
//        int totalFilteredRecords = finalFilteredList.size();
//        Function<List<ESBDepositCAAcctActivityInquiryTrnRecDTO>, List<DepositVATransHistSearchDTO>> trxMapper =
//                dtos -> finalFilteredList;
//
//        SearchCollectionResponse<DepositVATransHistSearchDTO> esbCATrxHists = asyncPaginationService.getPaginatedResponse(allDatas, trxMapper, Integer.MAX_VALUE, 1);
//        List<DepositVATransHistSearchDTO> depositVATransHistSearchDTOList = esbCATrxHists.getData();
//        //format the date from ESB resp
//        depositVATransHistSearchDTOList.forEach(dto -> {
//            try {
//                dto.setDate(DateTimeUtil.convertDepositCATrxHistDate(dto.getDate(), dto.getProcessingTime()));
//            } catch (Exception e) {
//                log.error("Error formatting ESB VA MY Activity Inquiry Date value for {}", dto.getDate());
//            }
//        });
//        esbCATrxHists.setTotalRecordCount(totalFilteredRecords);
//        try{
//            log.info("esbCATrxHists result --> {}", objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(esbCATrxHists));
//        } catch (Exception e){
//            log.info("error objectmapper -> {}", e);
//        }
//        // esb metadata injection
//        metadataService.attachMetadata(result, depositVATrxHistoryDTO);
//        depositVATrxHistoryDTO.setDatas(esbCATrxHists);
//        return depositVATrxHistoryDTO;
//    }
//
//    private List<Predicate<DepositVATransHistSearchDTO>> constructFilter(ESBDepositCAAcctActivityReqDTO receivedRequest) {
//        List<Predicate<DepositVATransHistSearchDTO>> filterConditions = new ArrayList<>();
//        //filter out invalid virtual acct numbers from ESB
//        Predicate<DepositVATransHistSearchDTO> vaAcctNumberParamFilter = dto ->
//                        !StringUtils.equals(dto.getVirtualAcctNumber(), "000000000000");
//        filterConditions.add(vaAcctNumberParamFilter);
//
//        Predicate<DepositVATransHistSearchDTO> searchParamFilter = dto ->
//                StringUtils.containsIgnoreCase(dto.getTrxDescription(), receivedRequest.getSearchParam()) ||
//                        StringUtils.containsIgnoreCase(dto.getVirtualAcctRefNumber(), receivedRequest.getSearchParam());
//
//        filterConditions.add(searchParamFilter);
//
//        if (StringUtils.isNotEmpty(receivedRequest.getTrxCode())) {
//            log.info("Filtering by trxCode: {}", receivedRequest.getTrxCode());
//            filterConditions.add(dto -> {
//                String dtoTrxCode = StringUtils.trimToEmpty(dto.getTrxCode());
//                String requestTrxCode = StringUtils.trimToEmpty(receivedRequest.getTrxCode());
//
//                boolean matches = StringUtils.equalsIgnoreCase(dtoTrxCode, requestTrxCode);
//                log.info("Checking trxCode: dtoTrxCode='{}' requestTrxCode='{}' match={}", dtoTrxCode, requestTrxCode, matches);
//                return matches;
//            });
//        }
//
//        if (StringUtils.isNotBlank(receivedRequest.getFilters().getStartDate()) &&
//                StringUtils.isNotBlank(receivedRequest.getFilters().getEndDate())) {
//
//            DateTimeFormatter esbDateFormat = DateTimeFormatter.ofPattern("yyyyMMdd");
//            DateTimeFormatter requestDateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");
//
//            try {
//                LocalDate startDate = LocalDate.parse(receivedRequest.getFilters().getStartDate(), requestDateFormat);
//                LocalDate endDate = LocalDate.parse(receivedRequest.getFilters().getEndDate(), requestDateFormat);
//
//                if (startDate.isAfter(endDate)) {
//                    throw new IllegalArgumentException("Start date cannot be after end date.");
//                }
//
//                filterConditions.add(dto -> {
//                    if (StringUtils.isBlank(dto.getDate())) return false;
//                    try {
//                        LocalDate trxDate = LocalDate.parse(dto.getDate(), esbDateFormat);
//                        return !trxDate.isBefore(startDate) && !trxDate.isAfter(endDate);
//                    } catch (DateTimeParseException e) {
//                        log.warn("Invalid date format in ESB data: {}", dto.getDate());
//                        return false;
//                    }
//                });
//            } catch (DateTimeParseException e) {
//                log.error("Invalid date format in request: startDate={}, endDate={}",
//                        receivedRequest.getFilters().getStartDate(), receivedRequest.getFilters().getEndDate());
//            }
//        }
//
//        if (StringUtils.isNotBlank(receivedRequest.getFilters().getStartTime()) &&
//                StringUtils.isNotBlank(receivedRequest.getFilters().getEndTime())) {
//
//            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
//
//            try {
//                LocalTime startTime = LocalTime.parse(receivedRequest.getFilters().getStartTime(), timeFormatter);
//                LocalTime endTime = LocalTime.parse(receivedRequest.getFilters().getEndTime(), timeFormatter);
//
//                filterConditions.add(dto -> {
//                    if (StringUtils.isBlank(dto.getProcessingTime())) return false;
//                    try {
//                        // Ensure processingTime is always 6 digits by prepending zeros
//                        String processingTime = StringUtils.leftPad(dto.getProcessingTime(), 6, '0');
//
//                        LocalTime trxTime = LocalTime.parse(processingTime, DateTimeFormatter.ofPattern("HHmmss"));
//                        return !trxTime.isBefore(startTime) && !trxTime.isAfter(endTime);
//                    } catch (DateTimeParseException e) {
//                        log.warn("Invalid time format in ESB data: {}", dto.getProcessingTime());
//                        return false;
//                    }
//                });
//            } catch (DateTimeParseException e) {
//                log.error("Invalid time format in request: startTime={}, endTime={}",
//                        receivedRequest.getFilters().getStartTime(), receivedRequest.getFilters().getEndTime());
//            }
//        }
//
//        if (StringUtils.isNotEmpty(receivedRequest.getFilters().getAmtFrom()) &&
//                StringUtils.isNotEmpty(receivedRequest.getFilters().getAmtTo())) {
//            log.info("executing amount filter : {} - {}",receivedRequest.getFilters().getAmtFrom(), receivedRequest.getFilters().getAmtTo());
//            double amtFrom = Double.parseDouble(receivedRequest.getFilters().getAmtFrom().replace(",", ""));
//            log.info("amtFrom value : {}", amtFrom);
//            double amtTo = Double.parseDouble(receivedRequest.getFilters().getAmtTo().replace(",", ""));
//            log.info("amtTo value : {}", amtTo);
//
//            filterConditions.add(dto -> {
//                // Parse credit and debit safely
//                Double credit = StringUtils.isNotEmpty(dto.getCredit()) ? Double.parseDouble(dto.getCredit().replace(",", "")) : null;
//                log.info("credit value : {}", credit);
//                Double debit = StringUtils.isNotEmpty(dto.getDebit()) ? Double.parseDouble(dto.getDebit().replace(",", "")) : null;
//                log.info("debit value : {}", debit);
//                // Compare values based on availability
//                if (credit != null && debit != null) {
//                    log.info("executing credit != null && debit != null");
//                    return (credit >= amtFrom && credit <= amtTo) || (debit >= amtFrom && debit <= amtTo);
//                } else if (credit != null) {
//                    log.info("executing credit != null");
//                    return credit >= amtFrom && credit <= amtTo;
//                } else if (debit != null) {
//                    log.info("executing debit != null");
//                    return debit >= amtFrom && debit <= amtTo;
//                } else {
//                    log.info("amount return false");
//                    return false;
//                }
//            });
//        }
//
//        return filterConditions;
//    }
//
//    private void crawling(ESBDTO result, ESBDepositCAAcctActivityReqDTO request, List<ESBDepositCAAcctActivityInquiryTrnRecDTO> allDatas) {
//        String lastKey = "";
//        long startTime = System.currentTimeMillis();
//        ESBPropertiesDTO configProperties = esbIntegrationProperties.getIbmMq(PropertyServiceEnum.DEPOSIT_CA_GETACCTACTIVITYINQUIRY_MY.getValue());
//
//        do {
//            ESBDepositCAAcctActivityInquiryMsgBodyDTO respMsgBody = objectMapper.convertValue(result.getMessage().getMsgBody(), ESBDepositCAAcctActivityInquiryMsgBodyDTO.class);
//            if (respMsgBody == null) {
//                log.error("Response message body conversion failed for ESB Deposit VA MY Activity Inquiry");
//                break;
//            }
//            List<ESBDepositCAAcctActivityInquiryTrnRecDTO> datas = respMsgBody.getAcctTrnRec();
//            if (datas != null) {
//                allDatas.addAll(datas);
//            }
//
//            lastKey = respMsgBody.getMbbLastKey();
//
//            if (Objects.isNull(lastKey) || lastKey.isEmpty()) {
//                log.error("mbbLastKey is null or empty for ESB Deposit VA MY Activity Inquiry, breaking out of the loop");
//                break;
//            } else if (!lastKey.equals(TERMINATING_KEY)
//                    && !lastKey.equals(STOP_KEY)
//                    && result.getMessage().getMsgHeader().getStatusCode().equals(ESBStatusCodeEnum.SUCCESS.getValue())) {
//                if(request.getMbbLastKey() != null){
//                    if(request.getMbbLastKey().equalsIgnoreCase(lastKey)){ //make sure to stop call ESB if mbbLastKey is repeated
//                        log.error("Previous and Next mbbLastKey is same --> {}", lastKey);
//                        break;
//                    }
//                }
//                request.setMbbLastKey(lastKey); //update new key here
//                ESBReqDTO esbReqDTO = ESBReqDTO.builder()
//                        .message(ESBMsgReqDTO.builder()
//                                .messageHeader(esbClient.constructMsgHeader(configProperties))
//                                .messageBody(createESBAcctActivityInquiryReqDTO(request)).build())
//                        .build();
//                result = executeSpecialCase(esbReqDTO);
//            }
//
//        } while (!(lastKey.equals(TERMINATING_KEY) || lastKey.equals(STOP_KEY))
//                && result.getMessage().getMsgHeader().getStatusCode().equals(ESBStatusCodeEnum.SUCCESS.getValue()));
//        long endTime = System.currentTimeMillis();
//        log.info("ESB for Deposit VA MY Activity Inquiry call duration (special case) : {} ms", (endTime - startTime));
//        log.info("Total records fetched from ESB for Deposit VA MY Activity Inquiry : {} ", allDatas.size());
//    }
//
//    public ESBDepositCAAcctActivityInquiryMsgBodyReqDTO createESBAcctActivityInquiryReqDTO(ESBDepositCAAcctActivityReqDTO request) {
//        ESBDTRangeReqDTO dtRange = ESBDTRangeReqDTO.builder()
//                .startDate(Objects.nonNull(request.getFilters()) && (org.springframework.util.StringUtils.hasText(request.getFilters().getStartDate()))
//                        ? getESBDate(request.getFilters().getStartDate())
//                        : getESBDefaultDate().get("START_DT"))
//                .endDate(Objects.nonNull(request.getFilters()) && (org.springframework.util.StringUtils.hasText(request.getFilters().getEndDate()))
//                        ? getESBDate(request.getFilters().getEndDate())
//                        : getESBDefaultDate().get("END_DT"))
//                .startTime(Objects.nonNull(request.getFilters()) && (org.springframework.util.StringUtils.hasText(request.getFilters().getStartTime()))
//                        ? getESBTime(request.getFilters().getStartTime())
//                        : getESBDefaultTime().get("START_TM"))
//                .endTime(Objects.nonNull(request.getFilters()) && (org.springframework.util.StringUtils.hasText(request.getFilters().getEndTime()))
//                        ? getESBTime(request.getFilters().getEndTime())
//                        : getESBDefaultTime().get("END_TM"))
//                .build();
//
//        return ESBDepositCAAcctActivityInquiryMsgBodyReqDTO.builder()
//                .accountNumber(request.getAccountNo())
//                .accountType("D")
//                .currency(request.getCurrency())
//                .mbbLastKey(request.getMbbLastKey())
//                .dtRange(dtRange)
//                .build();
//    }
//
//    @SneakyThrows
//    public <T> ESBDTO executeSpecialCase(ESBReqDTO request) {
//        String esbRequestJson = objectMapper.writeValueAsString(request);
//        log.info("executeSpecialCase :: Preparing call for ESB --> {}", esbRequestJson);
//        String resp = esbClient.callHost(objectMapper.writeValueAsString(request), PropertyServiceEnum.ESBMY);
//        log.info("executeSpecialCase :: Response payload for ESB --> {}", resp);
//
//        if (Objects.nonNull(resp)) {
//            ESBDTO result = objectMapper.readValue(resp, ESBDTO.class);
//            return result;
//        }
//        return new ESBDTO();
//    }
//}
