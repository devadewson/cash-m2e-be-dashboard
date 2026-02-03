package com.m2e.dashboard.service.af.fs.jms.account.activityinquiry;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.m2e.dashboard.config.jms.ESBIntegrationProperties;
import com.m2e.dashboard.config.jms.ESBServiceMapping;
import com.m2e.dashboard.dto.af.response.fs.AFFSTrxHistDTO;
import com.m2e.dashboard.dto.af.response.fs.AFFSTrxHistoryResp;
import com.m2e.dashboard.dto.common.SearchCollectionResponse;
import com.m2e.dashboard.enums.ESBServiceType;
import com.m2e.dashboard.enums.ESBStatusCodeEnum;
import com.m2e.dashboard.enums.PropertyServiceEnum;
import com.m2e.dashboard.jms.af.fs.request.account.activityinquiry.ESBAFFSAcctActivityInquiryMsgBodyReqDTO;
import com.m2e.dashboard.jms.af.fs.request.account.activityinquiry.ESBAFFSAcctActivityInquiryReqDTO;
import com.m2e.dashboard.jms.af.fs.response.account.activityinquiry.ESBAFFSAcctActivityInquiryMsgBodyDTO;
import com.m2e.dashboard.jms.af.fs.response.account.activityinquiry.ESBAFFSAcctActivityInquiryTrnRecDTO;
import com.m2e.dashboard.jms.common.ESBPropertiesDTO;
import com.m2e.dashboard.jms.common.request.ESBDTRangeReqDTO;
import com.m2e.dashboard.jms.common.request.ESBMsgReqDTO;
import com.m2e.dashboard.jms.common.request.ESBReqDTO;
import com.m2e.dashboard.jms.common.response.ESBDTO;
import com.m2e.dashboard.mapper.af.fs.ESBAFFSAcctActivityInquiryMapper;
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

import static com.m2e.dashboard.util.DateTimeUtil.*;
import static org.springframework.util.StringUtils.hasText;


@Service
@ESBServiceMapping(ESBServiceType.AF_FS_ACCT_ACTIVITY_INQUIRY_MY)
public class AFFSAcctActivityInquiryMYResponseProcessor implements ESBResponseProcessor<AFFSTrxHistoryResp> {

    private static final Logger log = LoggerFactory.getLogger(AFFSAcctActivityInquiryMYResponseProcessor.class);
    private final ObjectMapper objectMapper;
    private final ESBAFFSAcctActivityInquiryMapper esbaffsAcctActivityInquiryMapper;
    private final AsyncPaginationService asyncPaginationService;
    private final AsyncMappingService asyncMappingService;
    private final ESBClient esbClient;
    private final ESBMetadataService esbMetadataService;
    private final ESBIntegrationProperties esbIntegrationProperties;
    private static final String TERMINATING_KEY = "00000000";
    private static final String STOP_KEY = "0";

    @Autowired
    public AFFSAcctActivityInquiryMYResponseProcessor(ObjectMapper objectMapper, ESBAFFSAcctActivityInquiryMapper esbaffsAcctActivityInquiryMapper, AsyncPaginationService asyncPaginationService, AsyncMappingService asyncMappingService, ESBClient esbClient,
                                                      ESBIntegrationProperties esbIntegrationProperties, ESBMetadataService esbMetadataService) {
        this.objectMapper = objectMapper;
        this.esbaffsAcctActivityInquiryMapper = esbaffsAcctActivityInquiryMapper;
        this.asyncPaginationService = asyncPaginationService;
        this.asyncMappingService = asyncMappingService;
        this.esbClient = esbClient;
        this.esbIntegrationProperties = esbIntegrationProperties;
        this.esbMetadataService = esbMetadataService;
    }

    @Override
    public AFFSTrxHistoryResp processResponse(ESBDTO result, Object request) {
        ESBAFFSAcctActivityInquiryReqDTO receivedRequest = objectMapper.convertValue(request, ESBAFFSAcctActivityInquiryReqDTO.class);
        try {
            log.info("receivedRequest result --> {}", objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(receivedRequest));
        } catch (Exception e) {
            log.info("error objectmapper -> {}", e);
        }
        List<ESBAFFSAcctActivityInquiryTrnRecDTO> allDatas = new ArrayList<>();
        AFFSTrxHistoryResp affsTrxHistoryResp = new AFFSTrxHistoryResp();

        crawling(result, receivedRequest, allDatas);
        CompletableFuture<List<AFFSTrxHistDTO>> futureResult = asyncMappingService.mapToListAsync(allDatas, esbaffsAcctActivityInquiryMapper::mapToLoanTrxHistoryRes);

        List<Predicate<AFFSTrxHistDTO>> filterConditions = constructFilter(receivedRequest);

        List<Function<List<AFFSTrxHistDTO>, List<AFFSTrxHistDTO>>> sortFunctions = new ArrayList<>();
        sortFunctions.add(list -> list.stream()
                .sorted((a, b) -> b.getTrxDate().compareTo(a.getTrxDate()))
                .toList());

        CompletableFuture<List<AFFSTrxHistDTO>> filteredResult = futureResult.thenCompose(mappedList ->
                asyncMappingService.processListAsyncAndFilter(mappedList,
                        dto -> dto,
                        filterConditions,
                        sortFunctions)
        );

        List<AFFSTrxHistDTO> finalFilteredList = filteredResult.join();
        int totalFilteredRecords = finalFilteredList.size();
        Function<List<ESBAFFSAcctActivityInquiryTrnRecDTO>, List<AFFSTrxHistDTO>> trxMapper =
                dtos -> finalFilteredList;

        SearchCollectionResponse<AFFSTrxHistDTO> esbFSTrxHists = asyncPaginationService.getPaginatedResponse(allDatas, trxMapper, receivedRequest.getPageSize(), receivedRequest.getPage());
        List<AFFSTrxHistDTO> trxHistoryDTOList = esbFSTrxHists.getData();
        //format the date from ESB resp
        trxHistoryDTOList.forEach(dto -> {
            try {
                dto.setTrxDate(DateTimeUtil.convertCATrxHistDate(dto.getTrxDate()));
            } catch (Exception e) {
                log.error("Error formatting ESB AF FS MY Activity Inquiry Date value for {}", dto.getTrxDate());
            }
        });
        esbFSTrxHists.setTotalRecordCount(totalFilteredRecords);
        try {
            log.info("esbFSTrxHists result --> {}", objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(esbFSTrxHists));
        } catch (Exception e) {
            log.info("error objectmapper -> {}", e);
        }

        esbMetadataService.attachMetadata(result, affsTrxHistoryResp);
        affsTrxHistoryResp.setDatas(esbFSTrxHists);
        return affsTrxHistoryResp;
    }

    private List<Predicate<AFFSTrxHistDTO>> constructFilter(ESBAFFSAcctActivityInquiryReqDTO receivedRequest) {
        List<Predicate<AFFSTrxHistDTO>> filterConditions = new ArrayList<>();
        Predicate<AFFSTrxHistDTO> searchParamFilter = dto ->
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

    private void crawling(ESBDTO result, ESBAFFSAcctActivityInquiryReqDTO request, List<ESBAFFSAcctActivityInquiryTrnRecDTO> allDatas) {
        String lastKey = "";
        long startTime = System.currentTimeMillis();
        ESBPropertiesDTO configProperties = esbIntegrationProperties.getIbmMq(PropertyServiceEnum.AF_FS_GETACCTACTIVITYINQUIRY_MY.getValue());

        if(result.getMessage().getMsgBody() != null) {
            do {
                ESBAFFSAcctActivityInquiryMsgBodyDTO respMsgBody = objectMapper.convertValue(result.getMessage().getMsgBody(), ESBAFFSAcctActivityInquiryMsgBodyDTO.class);
                if (respMsgBody != null) {
                    List<ESBAFFSAcctActivityInquiryTrnRecDTO> datas = respMsgBody.getAcctTrnRec();
                    if (datas != null) {
                        allDatas.addAll(datas);
                    }

                    lastKey = respMsgBody.getMbbLastKey();

                    if (Objects.isNull(lastKey) || lastKey.isEmpty()) {
                        log.error("mbbLastKey is null or empty for ESB Autofinance FS MY Activity Inquiry, breaking out of the loop");
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
                    && result.getMessage().getMsgHeader().getStatusCode().equals(ESBStatusCodeEnum.SUCCESS.getValue()));
        }
        long endTime = System.currentTimeMillis();
        log.info("ESB for Auto Finance FS Activity Inquiry call duration (special case) : {} ms", (endTime - startTime));
        log.info("Total records fetched from ESB for Auto Finance FS Activity Inquiry : {} ", allDatas.size());
    }

    public ESBAFFSAcctActivityInquiryMsgBodyReqDTO createESBAcctActivityInquiryReqDTO(ESBAFFSAcctActivityInquiryReqDTO request) {
        return ESBAFFSAcctActivityInquiryMsgBodyReqDTO.builder()
                .accountNumber(request.getAcctNumber())
                .accountType("A")
                .currency(request.getCurrency())
                .mbbLastKey(request.getMbbLastKey())
                .dtRange(ESBDTRangeReqDTO.builder()
                        .startDate(Objects.nonNull(request.getFilters()) && (hasText(request.getFilters().getStartDate()))
                                ? getESBDate(request.getFilters().getStartDate())
                                : getESBDefaultDate().get("START_DT"))
                        .endDate(Objects.nonNull(request.getFilters()) && (hasText(request.getFilters().getEndDate()))
                                ? getESBDate(request.getFilters().getEndDate())
                                : getESBDefaultDate().get("END_DT"))
                        .startTime(Objects.nonNull(request.getFilters()) && (hasText(request.getFilters().getStartTime()))
                                ? getESBTime(request.getFilters().getStartTime())
                                : getESBDefaultTime().get("START_TM"))
                        .endTime(Objects.nonNull(request.getFilters()) && (hasText(request.getFilters().getEndTime()))
                                ? getESBTime(request.getFilters().getEndTime())
                                : getESBDefaultTime().get("END_TM"))
                        .build())
                .build();
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