//package com.m2e.dashboard.service.loan.tlf.jms.account.tranchdetails;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.m2e.dashboard.config.jms.ESBIntegrationProperties;
//import com.m2e.dashboard.config.jms.ESBServiceMapping;
//import com.m2e.dashboard.dto.loan.tlf.response.LNTLFTranchesDetailIslamicDTO;
//import com.m2e.dashboard.enums.ESBServiceType;
//import com.m2e.dashboard.enums.ESBStatusCodeEnum;
//import com.m2e.dashboard.enums.PropertyServiceEnum;
//import com.m2e.dashboard.jms.common.ESBPropertiesDTO;
//import com.m2e.dashboard.jms.common.request.ESBMsgReqDTO;
//import com.m2e.dashboard.jms.common.request.ESBReqDTO;
//import com.m2e.dashboard.jms.common.response.ESBDTO;
//import com.m2e.dashboard.jms.loan.tlf.request.tranch.details.ESBLoanTLFTranchDetailReqDTO;
//import com.m2e.dashboard.jms.loan.tlf.request.tranch.details.ESBLoanTLFTranchesDetailsReqDTO;
//import com.m2e.dashboard.jms.loan.tlf.response.account.details.ESBLoanTLFDetailDTO;
//import com.m2e.dashboard.jms.loan.tlf.response.tranch.details.ESBLoanTLFTranchesMsgBodyDTO;
//import com.m2e.dashboard.jms.loan.tlf.response.tranch.details.ESBLoanTLFTranchesTrnRecDTO;
//import com.m2e.dashboard.mapper.loan.tlf.ESBLoanTLFTranchDetailsMapper;
//import com.m2e.dashboard.service.esb.client.ESBClient;
//import com.m2e.dashboard.service.esb.ESBMetadataService;
//import com.m2e.dashboard.service.esb.ESBResponseProcessor;
//import lombok.SneakyThrows;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Objects;
//
//@Service
//@ESBServiceMapping(ESBServiceType.LOAN_TLF_TRANCH_DETAIL_INQUIRY_FOREIGN_ISLAMIC)
//public class LoanTLFTranchDetailsIslamicResponseProcessor implements ESBResponseProcessor<LNTLFTranchesDetailIslamicDTO> {
//    private static final Logger log = LoggerFactory.getLogger(LoanTLFTranchDetailsIslamicResponseProcessor.class);
//    private final ObjectMapper objectMapper;
//    private final ESBLoanTLFTranchDetailsMapper esbLoanTLFTranchDetailsMapper;
//    private final ESBMetadataService metadataService;
//    private final ESBClient esbClient;
//    private final ESBIntegrationProperties esbIntegrationProperties;
//    private static final String TERMINATING_KEY = "00000000";
//    private static final String STOP_KEY = "0";
//
//    @Autowired
//    public LoanTLFTranchDetailsIslamicResponseProcessor(ObjectMapper objectMapper, ESBLoanTLFTranchDetailsMapper esbLoanTLFTranchDetailsMapper, ESBMetadataService metadataService, ESBClient esbClient, ESBIntegrationProperties esbIntegrationProperties) {
//        this.objectMapper = objectMapper;
//        this.esbLoanTLFTranchDetailsMapper = esbLoanTLFTranchDetailsMapper;
//        this.metadataService = metadataService;
//        this.esbClient = esbClient;
//        this.esbIntegrationProperties = esbIntegrationProperties;
//    }
//
//    @Override
//    public LNTLFTranchesDetailIslamicDTO processResponse(ESBDTO result, Object request) {
//        ESBLoanTLFTranchDetailReqDTO esbLnTlfTranchesDetailReqDTO = objectMapper.convertValue(request, ESBLoanTLFTranchDetailReqDTO.class);
//        return crawling(result, esbLnTlfTranchesDetailReqDTO);
//    }
//
//    private LNTLFTranchesDetailIslamicDTO crawling(ESBDTO result, ESBLoanTLFTranchDetailReqDTO request) {
//        LNTLFTranchesDetailIslamicDTO tranchesDetailIslamicDTO = new LNTLFTranchesDetailIslamicDTO();
//        String lastKey = "";
//        long startTime = System.currentTimeMillis();
//        ESBPropertiesDTO configProperties = esbIntegrationProperties.getIbmMq(PropertyServiceEnum.LOAN_TLF_GETTRANCHDETAILS_FOREIGN_ISLAMIC.getValue());
//
//        if(result.getMessage().getMsgBody() != null) {
//            do {
//                ESBLoanTLFTranchesMsgBodyDTO respMsgBody = objectMapper.convertValue(result.getMessage().getMsgBody(), ESBLoanTLFTranchesMsgBodyDTO.class);
//                if (respMsgBody == null) {
//                    log.error("Response message body conversion failed for ESB Loan TLF Activity Inquiry");
//                    break;
//                }
//
//                List<ESBLoanTLFTranchesTrnRecDTO> tranchDetailsList = respMsgBody.getTranchDetails();
//                List<ESBLoanTLFDetailDTO> tranchKeyValueList = new ArrayList<>();
//                if (tranchDetailsList != null && !tranchDetailsList.isEmpty()) {
//                    for (ESBLoanTLFTranchesTrnRecDTO tranchDetail : tranchDetailsList) {
//                        log.info("Looping through tranchDetailsList to get tranchDetail");
//                        ESBLoanTLFDetailDTO esbLoanTLFDetailDTO = tranchDetail.getDetails().stream().filter(detail -> detail.getName().equalsIgnoreCase("TranchNo") && detail.getValue().equals(request.getTrancheNumber())
//                        ).findFirst().orElse(null);
//
//                        log.info("esbLoanTLFDetailDTO: {}", esbLoanTLFDetailDTO);
//                        if (esbLoanTLFDetailDTO != null) {
//                            log.info("found tranch from ESB List");
//                            tranchKeyValueList = tranchDetail.getDetails();
//                            lastKey = TERMINATING_KEY;
//                            break;
//                        }
//                    }
//                    if (!tranchKeyValueList.isEmpty()) {
//                        tranchesDetailIslamicDTO = esbLoanTLFTranchDetailsMapper.mapToTermLoanTranchDetailIslamicDTO(tranchKeyValueList, request.getCurrency());
//                    }
//                }
//
//                if (!TERMINATING_KEY.equals(lastKey)) lastKey = respMsgBody.getMbbLastKey();
//
//                if (Objects.isNull(lastKey) || lastKey.isEmpty()) {
//                    log.error("mbbLastKey is null or empty for ESB Loan TL Tranches Inquiry, breaking out of the loop");
//                    break;
//                } else if (!lastKey.equals(TERMINATING_KEY)
//                        && !lastKey.equals(STOP_KEY)
//                        && result.getMessage().getMsgHeader().getStatusCode().equals(ESBStatusCodeEnum.SUCCESS.getValue())) {
//                    if (request.getMbbLastKey() != null) {
//                        if (request.getMbbLastKey().equalsIgnoreCase(lastKey)) { //make sure to stop call ESB if mbbLastKey is repeated
//                            log.error("Previous and Next mbbLastKey is same --> {}", lastKey);
//                            break;
//                        }
//                    }
//                    request.setMbbLastKey(lastKey); //update new key here
//                    ESBReqDTO esbReqDTO = ESBReqDTO.builder()
//                            .message(ESBMsgReqDTO.builder()
//                                    .messageHeader(esbClient.constructMsgHeader(configProperties))
//                                    .messageBody(createESBTranchesInquiryReqDTO(request)).build())
//                            .build();
//                    result = executeSpecialCase(esbReqDTO);
//                }
//            } while (!(lastKey.equals(TERMINATING_KEY) || lastKey.equals(STOP_KEY))
//                    && result.getMessage().getMsgHeader().getStatusCode().equals(ESBStatusCodeEnum.SUCCESS.getValue()));
//        }
//        long endTime = System.currentTimeMillis();
//        log.info("crawling() tranchesDetailIslamicDTO: {}", tranchesDetailIslamicDTO);
//        log.info("ESB for Loan TL Tranches Inquiry call duration (special case) : {} ms", (endTime - startTime));
//        // esb metadata injection
//        metadataService.attachMetadata(result, tranchesDetailIslamicDTO);
//        return tranchesDetailIslamicDTO;
//    }
//
//    public ESBLoanTLFTranchesDetailsReqDTO createESBTranchesInquiryReqDTO(ESBLoanTLFTranchDetailReqDTO request) {
//        return ESBLoanTLFTranchesDetailsReqDTO.builder()
//                .accountNo(request.getAcctNumber())
//                .accountType("L")
//                .currency(request.getCurrency())
//                .mbbLastKey(request.getMbbLastKey())
//                .build();
//    }
//
//    @SneakyThrows
//    public <T> ESBDTO executeSpecialCase(ESBReqDTO request) {
//        log.info("executeSpecialCase :: Preparing call for ESB --> {}", request);
//        String resp = esbClient.callHost(objectMapper.writeValueAsString(request), PropertyServiceEnum.ESBMY);
//        log.info("executeSpecialCase :: Response payload for ESB --> {}", resp);
//
//        if (Objects.nonNull(resp)) {
//            return objectMapper.readValue(resp, ESBDTO.class);
//        }
//        return new ESBDTO();
//    }
//}
