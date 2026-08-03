//package com.m2e.dashboard.service.deposit.fd.jms.account.certdetails.my;
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
//import com.m2e.dashboard.jms.common.response.ESBDetailDTO;
//import com.m2e.dashboard.jms.deposit.fd.request.account.certdetails.ESBDepositFDCertDetailsReqDTO;
//import com.m2e.dashboard.jms.deposit.fd.request.account.certdetails.ESBDepositFDCertsReqDTO;
//import com.m2e.dashboard.jms.deposit.fd.response.account.certdetails.ESBDepositFDCertsDTO;
//import com.m2e.dashboard.mapper.deposit.fd.ESBFDAcctCertDetailsMapper;
//import com.m2e.dashboard.service.*;
//import com.m2e.dashboard.service.esb.client.ESBClient;
//import com.m2e.dashboard.service.esb.ESBMetadataService;
//import com.m2e.dashboard.service.esb.ESBResponseProcessor;
//import com.m2e.dashboard.util.ESBValueUtil;
//import com.m2e.utils.common.DateTimeUtil;
//import com.m2e.utils.common.enums.DateTimeFormatPatternEnum;
//import com.m2e.utils.esb.enums.deposit.fd.CertificateStatus;
//import com.m2e.utils.esb.enums.deposit.fd.LcrIndicator;
//import lombok.RequiredArgsConstructor;
//import lombok.SneakyThrows;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.lang.StringUtils;
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
//@RequiredArgsConstructor
//@Slf4j
//@Service
//@ESBServiceMapping(ESBServiceType.DEPOSIT_FD_ACCT_CERT_DETAIL_INQUIRY_MY)
//public class DepositFDAcctCertDetailsInquiryMYResponseProcessor implements ESBResponseProcessor<DepositFDCertificateListDTO> {
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
//    @Override
//    public DepositFDCertificateListDTO processResponse(ESBDTO result, Object request) {
//        ESBDepositFDCertsReqDTO receivedRequest = objectMapper.convertValue(request, ESBDepositFDCertsReqDTO.class);
//        ESBDepositFDCertsDTO respMsgBody = objectMapper.convertValue(result.getMessage().getMsgBody(), ESBDepositFDCertsDTO.class);
//        DepositFDCertificateListDTO depositFDCertificateListDTO = new DepositFDCertificateListDTO();
//        List<List<ESBDetailDTO>> allDatas = new ArrayList<>();
//        crawling(result, receivedRequest, allDatas);
//
//        CompletableFuture<List<DepositFDCertificateDetailDTO.FullData>> futureResult = asyncMappingService.mapToListAsync(allDatas, esbFDAcctCertDetailsMapper::mapToDepositFDCertDetailMYResp);
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
//        Function<List<List<ESBDetailDTO>>, List<DepositFDCertificateDetailDTO.FullData>> trxMapper =
//                dtos -> filteredResult.join();
//        SearchCollectionResponse<DepositFDCertificateDetailDTO.FullData> esbFDCertsList = asyncPaginationService.getPaginatedResponse(allDatas, trxMapper, receivedRequest.getPageSize(), receivedRequest.getPage());
//        List<DepositFDCertificateDetailDTO.FullData> formattedList = esbFDCertsList.getData();
//        //format the esb data
//        formattedList.forEach(dto -> {
//            CertificateStatus certStatus = CertificateStatus.fromCode(dto.getCertStatusCode());
//            LcrIndicator lcrIndicator = LcrIndicator.fromCode(dto.getLcrIndicator());
//            if (certStatus == CertificateStatus.ACTIVE && lcrIndicator == LcrIndicator.UPLIFT_TAGGED) {
//                certStatus = CertificateStatus.UPLIFTMENT_IN_31_DAYS;
//            }
//            dto.setCertStatusCode(certStatus.name());
//            dto.setCertStatusDesc(certStatus.getDescription());
//            dto.setTenure(ESBValueUtil.formatTenure(dto.getTenure()));
//            dto.setPlacementDate(DateTimeUtil.formatDate(dto.getPlacementDate(), DateTimeFormatPatternEnum.yyyyMMdd, DateTimeFormatPatternEnum.d_MMM_yyyy));
//            dto.setMaturityDate(DateTimeUtil.formatDate(dto.getMaturityDate(), DateTimeFormatPatternEnum.yyyyMMdd, DateTimeFormatPatternEnum.d_MMM_yyyy));
//            dto.setLcrDate(DateTimeUtil.formatDate(dto.getLcrDate(), DateTimeFormatPatternEnum.yyyyMMdd, DateTimeFormatPatternEnum.d_MMM_yyyy));
//            dto.setInstructionOnMaturity(ESBValueUtil.getInstructionMaturityMY(dto.getInstructionOnMaturity()));
//            dto.setInterestRate(formatInterestRate(dto.getInterestRate()));
//            dto.setCurrency(respMsgBody.getCurrency());
//            dto.setInterestPaymentMode(ESBValueUtil.getInterestPaymentModeValueMY(dto.getInterestPaymentMode()));
//            dto.setInterestPaymentFrequency(ESBValueUtil.getInterestPaymentFrequencyValueMY(dto.getInterestPaymentFrequency()));
//            dto.setProfitSharingRatio(formatRatio(dto.getProfitSharingRatio()));
//        });
//        depositFDCertificateListDTO.setDatas(esbFDCertsList);
//        // esb metadata injection
//        metadataService.attachMetadata(result, depositFDCertificateListDTO);
//        return depositFDCertificateListDTO;
//    }
//
//    private void crawling(ESBDTO result, ESBDepositFDCertsReqDTO request, List<List<ESBDetailDTO>> allDatas) {
//        String lastKey = "";
//        long startTime = System.currentTimeMillis();
//        ESBPropertiesDTO configProperties = esbIntegrationProperties.getIbmMq(PropertyServiceEnum.DEPOSIT_FD_GETCERTDETAILS_MY.getValue());
//
//        do {
//            ESBDepositFDCertsDTO respMsgBody = objectMapper.convertValue(result.getMessage().getMsgBody(), ESBDepositFDCertsDTO.class);
//            if (respMsgBody == null) {
//                log.error("Response message body conversion failed for ESB Deposit FD MY Cert Detail Inquiry");
//                break;
//            }
//            List<List<ESBDetailDTO>> datas = respMsgBody.getDetails();
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
//        log.info("ESB for Deposit FD MY Cert Detail Inquiry call duration (special case) : {} ms", (endTime - startTime));
//        log.info("Total records fetched from ESB for Deposit FD MY Cert Detail Inquiry : {} ", allDatas.size());
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
//        String resp = esbClient.callHost(objectMapper.writeValueAsString(request), PropertyServiceEnum.ESBMY);
//        log.info("executeSpecialCase :: Response payload for ESB --> {}", resp);
//
//        if (Objects.nonNull(resp)) {
//            ESBDTO result = objectMapper.readValue(resp, ESBDTO.class);
//            return result;
//        }
//        return new ESBDTO();
//    }
//
//    private static String formatInterestRate(String interestRate) {
//        if (interestRate == null || interestRate.isEmpty()) {
//            return interestRate;
//        }
//        BigDecimal rate = new BigDecimal(interestRate).setScale(2, RoundingMode.HALF_UP);
//        return rate.toString();
//    }
//
//    private static String formatRatio(String s) {
//        if (s == null || !s.matches("\\d{4}")) {
//            log.warn("formatRatio failed for interest rate {}", s);
//            return s;
//        }
//
//        int xx = Integer.parseInt(s.substring(0, 2)); // trims leading zeros
//        int yy = Integer.parseInt(s.substring(2, 4)); // trims leading zeros
//
//        return xx + ":" + yy;
//    }
//}
