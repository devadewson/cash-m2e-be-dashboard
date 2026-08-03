//package com.m2e.dashboard.service.deposit.fd.jms.account.certdetails.sg;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.m2e.dashboard.config.jms.ESBIntegrationProperties;
//import com.m2e.dashboard.config.jms.ESBServiceMapping;
//import com.m2e.dashboard.dto.common.SearchCollectionResponse;
//import com.m2e.dashboard.dto.deposit.fd.response.DepositFDCertificateDetailDTO;
//import com.m2e.dashboard.dto.deposit.fd.response.DepositFDCertificateListDTO;
//import com.m2e.dashboard.enums.ESBServiceType;
//import com.m2e.dashboard.enums.ESBStatusCodeEnum;
//import com.m2e.dashboard.enums.PropertyServiceEnum;
//import com.m2e.dashboard.jms.common.ESBPropertiesDTO;
//import com.m2e.dashboard.jms.common.request.ESBMsgReqDTO;
//import com.m2e.dashboard.jms.common.request.ESBReqDTO;
//import com.m2e.dashboard.jms.common.response.ESBDTO;
//import com.m2e.dashboard.jms.deposit.fd.request.account.certdetails.ESBDepositFDCertDetailsReqDTO;
//import com.m2e.dashboard.jms.deposit.fd.request.account.certdetails.ESBDepositFDCertsReqDTO;
//import com.m2e.dashboard.jms.deposit.fd.response.account.certdetails.ESBSGDepositFDCertDTO;
//import com.m2e.dashboard.jms.deposit.fd.response.account.certdetails.ESBSGDepositFDCertsDTO;
//import com.m2e.dashboard.mapper.deposit.fd.ESBFDAcctCertDetailsMapper;
//import com.m2e.dashboard.service.*;
//import com.m2e.dashboard.service.esb.client.ESBClient;
//import com.m2e.dashboard.service.esb.ESBMetadataService;
//import com.m2e.dashboard.service.esb.ESBResponseProcessor;
//import com.m2e.dashboard.util.ESBValueUtil;
//import lombok.SneakyThrows;
//import org.apache.commons.lang.StringUtils;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.math.BigDecimal;
//import java.math.RoundingMode;
//import java.text.ParseException;
//import java.text.SimpleDateFormat;
//import java.util.*;
//import java.util.concurrent.CompletableFuture;
//import java.util.function.Function;
//import java.util.function.Predicate;
//import java.util.stream.Collectors;
//
//
//@Service
//@ESBServiceMapping(ESBServiceType.DEPOSIT_FD_ACCT_CERT_DETAIL_INQUIRY_SG)
//public class DepositFDAcctCertDetailsInquirySGResponseProcessor implements ESBResponseProcessor<DepositFDCertificateListDTO> {
//    private static final Logger log = LoggerFactory.getLogger(DepositFDAcctCertDetailsInquirySGResponseProcessor.class);
//    private final ObjectMapper objectMapper;
//    private final ESBFDAcctCertDetailsMapper esbFDAcctCertDetailsMapper;
//    private final AsyncMappingService asyncMappingService;
//    private final AsyncPaginationService asyncPaginationService;
//    private final ESBClient esbClient;
//    private final ESBIntegrationProperties esbIntegrationProperties;
//    private final ESBMetadataService metadataService;
//    private static final String TERMINATING_KEY = "00000000";
//    private static final String STOP_KEY = "0";
//
//    @Autowired
//    public DepositFDAcctCertDetailsInquirySGResponseProcessor(ObjectMapper objectMapper, ESBFDAcctCertDetailsMapper esbFDAcctCertDetailsMapper, AsyncPaginationService asyncPaginationService, AsyncMappingService asyncMappingService, ESBClient esbClient, ESBIntegrationProperties esbIntegrationProperties, ESBMetadataService metadataService) {
//        this.objectMapper = objectMapper;
//        this.esbFDAcctCertDetailsMapper = esbFDAcctCertDetailsMapper;
//        this.asyncPaginationService = asyncPaginationService;
//        this.asyncMappingService = asyncMappingService;
//        this.esbClient = esbClient;
//        this.esbIntegrationProperties = esbIntegrationProperties;
//        this.metadataService = metadataService;
//    }
//
//    @Override
//    public DepositFDCertificateListDTO processResponse(ESBDTO result, Object request) {
//        ESBDepositFDCertsReqDTO receivedRequest = objectMapper.convertValue(request, ESBDepositFDCertsReqDTO.class);
//        DepositFDCertificateListDTO depositFDCertificateListDTO = new DepositFDCertificateListDTO();
//        List<ESBSGDepositFDCertsDTO> allDatas = new ArrayList<>();
//        crawling(result, receivedRequest, allDatas);
//
//        CompletableFuture<List<DepositFDCertificateDetailDTO.FullData>> futureResult = asyncMappingService.mapToListAsync(allDatas, esbFDAcctCertDetailsMapper::mapToDepositFDCertDetailSGResp);
//
//        //combine the predicates using OR logic
//        Predicate<DepositFDCertificateDetailDTO.FullData> combinedFilterCondition = dto ->
//                StringUtils.containsIgnoreCase(dto.getCertificateNumber(), receivedRequest.getSearchParam()) ||
//                        StringUtils.containsIgnoreCase(dto.getPreferredName(), receivedRequest.getSearchParam()) ||
//                        StringUtils.containsIgnoreCase(dto.getCurrency(), receivedRequest.getSearchParam());
//
//        List<Function<List<DepositFDCertificateDetailDTO.FullData>, List<DepositFDCertificateDetailDTO.FullData>>> sortFunctions = new ArrayList<>();
//        sortFunctions.add(list -> list.stream()
//                .filter(dto -> dto.getMaturityDate() != null)
//                .sorted((a, b) -> b.getMaturityDate().compareTo(a.getMaturityDate()))
//                .collect(Collectors.toList()));
//
//        CompletableFuture<List<DepositFDCertificateDetailDTO.FullData>> filteredResult = futureResult.thenCompose(mappedList ->
//                asyncMappingService.processListAsync(mappedList,
//                        dto -> dto,
//                        Collections.singletonList(combinedFilterCondition),
//                        sortFunctions)
//        );
//        Function<List<ESBSGDepositFDCertsDTO>, List<DepositFDCertificateDetailDTO.FullData>> trxMapper =
//                dtos -> filteredResult.join();
//        SearchCollectionResponse<DepositFDCertificateDetailDTO.FullData> esbFDCertsList = asyncPaginationService.getPaginatedResponse(allDatas, trxMapper, receivedRequest.getPageSize(), receivedRequest.getPage());
//        List<DepositFDCertificateDetailDTO.FullData> formattedList = esbFDCertsList.getData();
//        //format the esb data
//        formattedList.forEach(dto -> {
//            try {
//                dto.setPrincipalAmount(ESBValueUtil.formatSgEsbAmountValue(dto.getPrincipalAmount()));
//                dto.setMaturityInterest(ESBValueUtil.formatSgEsbAmountValue(dto.getMaturityInterest()));
//                dto.setTenure(ESBValueUtil.formatTenure(dto.getTenure()));
//                dto.setPlacementDate(formatDate(dto.getPlacementDate()));
//                dto.setMaturityDate(formatDate(dto.getMaturityDate()));
//                dto.setInstructionOnMaturity(ESBValueUtil.getInstructionMaturityValue(dto.getInstructionOnMaturity()));
//                dto.setCertificateNumber(ESBValueUtil.removeLeadingZeroes(dto.getCertificateNumber()));
//                dto.setInterestRate(formatSGInterestRate(dto.getInterestRate()));
//                dto.setCurrency(dto.getCurrency());
//                dto.setInterestPaymentMode(ESBValueUtil.getInterestPaymentModeValueSG(dto.getInterestPaymentMode()));
//            } catch (Exception e) {
//                log.error("Error formatting ESB SG FD Cert Inquiry response for cert: {}", dto.getCertificateNumber(), e);
//            }
//        });
//        depositFDCertificateListDTO.setDatas(esbFDCertsList);
//        // esb metadata injection
//        metadataService.attachMetadata(result, depositFDCertificateListDTO);
//        return depositFDCertificateListDTO;
//    }
//
//    private void crawling(ESBDTO result, ESBDepositFDCertsReqDTO request, List<ESBSGDepositFDCertsDTO> allDatas) {
//        String lastKey = "";
//        long startTime = System.currentTimeMillis();
//        ESBPropertiesDTO configProperties = esbIntegrationProperties.getIbmMq(PropertyServiceEnum.DEPOSIT_FD_GETCERTDETAILS_SG.getValue());
//
//        do {
//            ESBSGDepositFDCertDTO respMsgBody = objectMapper.convertValue(result.getMessage().getMsgBody(), ESBSGDepositFDCertDTO.class);
//            if (respMsgBody == null) {
//                log.error("Response message body conversion failed for ESB Deposit FD SG Cert Detail Inquiry");
//                break;
//            }
//            List<ESBSGDepositFDCertsDTO> datas = respMsgBody.getAccounts();
//            if (datas != null) {
//                allDatas.addAll(datas);
//            }
//
//            lastKey = respMsgBody.getMbbLastKey();
//
//            if (Objects.isNull(lastKey) || lastKey.isEmpty()) {
//                log.error("mbbLastKey is null or empty for ESB Deposit FD MY Cert Detail Inquiry, breaking out of the loop");
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
//                                .messageBody(createESBAcctBalanceInquiryReqDTO(request)).build())
//                        .build();
//                result = executeSpecialCase(esbReqDTO);
//            }
//
//        } while (!(lastKey.equals(TERMINATING_KEY) || lastKey.equals(STOP_KEY))
//                && result.getMessage().getMsgHeader().getStatusCode().equals(ESBStatusCodeEnum.SUCCESS.getValue()));
//        long endTime = System.currentTimeMillis();
//        log.info("ESB for Deposit FD SG Cert Detail Inquiry call duration (special case) : {} ms", (endTime - startTime));
//        log.info("Total records fetched from ESB for Deposit FD SG Cert Detail Inquiry : {} ", allDatas.size());
//    }
//
//    public ESBDepositFDCertDetailsReqDTO createESBAcctBalanceInquiryReqDTO(ESBDepositFDCertsReqDTO request) {
//        return ESBDepositFDCertDetailsReqDTO.builder()
//                .accountNo(request.getAccountNo())
//                .accountType("T")
//                .currency(request.getCurrency())
//                .mbbLastKey(request.getMbbLastKey())
//                .build();
//    }
//
//    @SneakyThrows
//    public <T> ESBDTO executeSpecialCase(ESBReqDTO request) {
//        String esbRequestJson = objectMapper.writeValueAsString(request);
//        log.info("executeSpecialCase :: Preparing call for ESB --> {}", esbRequestJson);
//        String resp = esbClient.callHost(objectMapper.writeValueAsString(request), PropertyServiceEnum.ESBSG);
//        log.info("executeSpecialCase :: Response payload for ESB --> {}", resp);
//
//        if (Objects.nonNull(resp)) {
//            ESBDTO result = objectMapper.readValue(resp, ESBDTO.class);
//            return result;
//        }
//        return new ESBDTO();
//    }
//
//    private String formatDate(String date) throws ParseException {
//        if (date == null || date.trim().isEmpty()) {
//            return "";
//        }
//        try {
//            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyyMMdd");
//            SimpleDateFormat outputFormat = new SimpleDateFormat("d MMM yyyy");
//            Date parsedDate = inputFormat.parse(date);
//            return outputFormat.format(parsedDate);
//        } catch (ParseException e) {
//            log.info("Error while format date for FD SG cert esb response processor");
//            return "";
//        }
//    }
//
//    private String formatSGInterestRate(String interestRate) {
//        if (interestRate == null || interestRate.isEmpty()) {
//            return interestRate;
//        }
//        BigDecimal rawValue = new BigDecimal(interestRate);
//        BigDecimal scaledValue = rawValue.movePointLeft(7).setScale(2, RoundingMode.HALF_UP);
//        return scaledValue.toPlainString();
//    }
//}
