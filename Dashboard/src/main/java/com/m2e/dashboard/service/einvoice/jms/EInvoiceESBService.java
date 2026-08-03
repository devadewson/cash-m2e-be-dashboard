//package com.m2e.dashboard.service.einvoice.jms;
//
//import com.fasterxml.jackson.databind.DeserializationFeature;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.google.common.base.Strings;
//import com.m2e.dashboard.dto.common.ErrorResponse;
//import com.m2e.dashboard.dto.einvoice.request.EInvoiceCorpDetailReqDTO;
//import com.m2e.dashboard.dto.einvoice.response.EInvoiceCorpDetailDTO;
//import com.m2e.dashboard.jms.einvoice.request.tupdateParty.*;
//import com.m2e.dashboard.jms.einvoice.request.tviewParty.*;
//import com.m2e.dashboard.jms.einvoice.request.tviewParty.MsgHeader;
//import com.m2e.dashboard.jms.einvoice.response.tviewParty.ESBTViewPartyFullMsgBodyDTO;
//import com.m2e.dashboard.mapper.einvoice.EInvoiceDetailsMapper;
//import com.m2e.dashboard.service.MDMClient;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import com.m2e.dashboard.dto.common.ApiResponse;
//import com.m2e.dashboard.dto.einvoice.request.EInvoiceValidateTINReqDTO;
//import com.m2e.dashboard.dto.einvoice.response.EInvoiceValidateTINDTO;
//import com.m2e.dashboard.enums.PropertyServiceEnum;
//import com.m2e.dashboard.service.esb.ESBClientService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//
//import java.time.LocalDateTime;
//import java.time.format.DateTimeFormatter;
//import java.util.ArrayList;
//import java.util.List;
//
//import static com.m2e.dashboard.enums.ESBServiceType.EINVOICE_VALIDATETIN_MY;
//
//@Service
//@RequiredArgsConstructor
//public class EInvoiceESBService {
//
//    @Value("${my.cif.detail.svcid}")
//    private String myCifDetailSvcId;
//    @Value("${my.cif.detail.txncode}")
//    private String myCifDetailTxnCode;
//    @Value("${my.cif.detail.txntype}")
//    private String myCifDetailTxnType;
//    @Value("${my.cif.detail.requestor}")
//    private String myCifDetailRequestor;
//    @Value("${my.cif.detail.countryOfOnboarding}")
//    private String myCifDetailCountry;
//    @Value("${my.cif.detail.sourceSystem}")
//    private String myCifDetailSource;
//
//    @Value("${my.cif.update.svcid}")
//    private String myCifUpdateSvcId;
//    @Value("${my.cif.update.txncode}")
//    private String myCifUpdateTxnCode;
//
//    @Autowired
//    private final MDMClient mdmClient;
//
//    private final ObjectMapper objectMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
//    private static final Logger logger = LoggerFactory.getLogger(EInvoiceESBService.class);
//    private final ESBClientService esbClientService;
//    private static final DateTimeFormatter reqFormatter = DateTimeFormatter.ofPattern("yyyyMMdd:HHmmss");
//    @Autowired
//    private EInvoiceDetailsMapper eInvoiceDetailsMapper;
//
//    public ApiResponse<EInvoiceValidateTINDTO> validateTin(EInvoiceValidateTINReqDTO request) {
//        ApiResponse<EInvoiceValidateTINDTO> response = esbClientService.execute(request, EINVOICE_VALIDATETIN_MY, EInvoiceValidateTINDTO::new, PropertyServiceEnum.ESBMYV23);
//        return response;
//    }
//
//    public ESBTViewPartyFullMsgBodyDTO cifInquiry(String cifNo) {
//        String applicationId = "";
//        try {
//            String jsonStringView = objectMapper.writeValueAsString(this.setCifPayloadReq(cifNo));
//            logger.info("DETAIL CIF " + applicationId + ":: " + jsonStringView);
//            return mdmClient.getCifDetail(jsonStringView, applicationId);
//        } catch (
//                RuntimeException exc) {
//            throw exc;
//        } catch (
//                Exception exc) {
//            logger.error(exc.getMessage(), exc);
//        }
//        return null;
//    }
//
//
//    public void updateParty(ESBTViewPartyFullMsgBodyDTO respBody, String tinNo, String sstNo) {
//        String applicationId = "";
//        try {
//            String jsonStringView = objectMapper.writeValueAsString(this.setUpdatePartyPayload(respBody, tinNo, sstNo));
//            logger.info("UPDATE PARTY REQUEST " + applicationId + ":: " + jsonStringView);
//            mdmClient.updatePartyDetails(jsonStringView, applicationId);
//        } catch (RuntimeException exc) {
//            throw exc;
//        } catch (Exception exc) {
//            logger.error(exc.getMessage(), exc);
//        }
//    }
//
//
//    public ApiResponse<EInvoiceCorpDetailDTO> getCorpDetail(ESBTViewPartyFullMsgBodyDTO msgBody,
//                                                            EInvoiceCorpDetailReqDTO brnReq) {
//        ApiResponse<EInvoiceCorpDetailDTO> response = new ApiResponse<>();
//        response.setMsgHeader(new ErrorResponse("0"));
//        EInvoiceCorpDetailDTO result = eInvoiceDetailsMapper.mapHostToDetails(msgBody);
//        result.setBrn(brnReq.getBrn());
//        result.setStatus(brnReq.getStatus());
//        response.setMsgBody(result);
//        return response;
//    }
//
//    public ESBViewPartyMsgBodyReqDTO setCifPayloadReq(String cif) {
//        ESBViewPartyMsgBodyReqDTO payload = new ESBViewPartyMsgBodyReqDTO();
//        ViewPartyMsgReq msgReq = new ViewPartyMsgReq();
//        MsgHeader msgHeader = new MsgHeader();
//
//        msgHeader.setMsgID(mdmClient.generateMsgId());
//        msgHeader.setSvcID(myCifDetailSvcId);
//        msgHeader.setTxnCode(myCifDetailTxnCode);
//        msgHeader.setEnv(mdmClient.getEnv());
//        msgHeader.setVer(mdmClient.getMyVer());
//        msgReq.setMsgHeader(msgHeader);
//
//        DetailReq detail = new DetailReq();
//        detail.setRequestor(myCifDetailRequestor);
//        detail.setTransactionType(myCifDetailTxnType);
//
//        LocalCIFReq localCIF = new LocalCIFReq();
//        localCIF.setCifNumber(Strings.nullToEmpty(cif).trim());
//        localCIF.setCountryOfOnboarding(myCifDetailCountry);
//        localCIF.setSourceSystem(myCifDetailSource);
//        detail.setLocalCIF(localCIF);
//
//        PartyClassReq partyClass = new PartyClassReq();
//        partyClass.setCode("0");
//        detail.setPartyClass(partyClass);
//
//        UniversalCIF universalCIF = new UniversalCIF();
//        universalCIF.setCifNumber(Strings.nullToEmpty(cif));
//        detail.setUniversalCIF(universalCIF);
//
//        msgReq.setMsgBody(detail);
//
//        payload.setMsgReq(msgReq);
//        return payload;
//    }
//
//
//    public ESBUpdatePartyMsgBodyReqDTO setUpdatePartyPayload(ESBTViewPartyFullMsgBodyDTO response, String tinNo, String sstNo) {
//        LocalDateTime localDateTime = LocalDateTime.now();
//        ESBUpdatePartyMsgBodyReqDTO payload = new ESBUpdatePartyMsgBodyReqDTO();
//        Msg msg = new Msg();
//        MsgBody msgReq = new MsgBody();
//        UpdatePartyMsgHeader msgHeader = new UpdatePartyMsgHeader();
//
//        msgHeader.setMsgID(mdmClient.generateMsgId());
//        msgHeader.setSvcID(myCifUpdateSvcId);
//        msgHeader.setTxnCode(myCifUpdateTxnCode);
//        msgHeader.setEnv(mdmClient.getEnv());
//        msgHeader.setVer(mdmClient.getMyVer());
//        msgHeader.setTxnDt(reqFormatter.format(localDateTime));
//        msg.setMsgHeader(msgHeader);
//
//        msgReq.setRequestor(myCifDetailRequestor);
//        msgReq.setTransactionType("UPDATE");
//
//        CrsListItem crsListItem = new CrsListItem();
//
//        TaxRegistredCountry taxRegistredCountry = new TaxRegistredCountry();
//        if (response.getCrsList() != null){
//            taxRegistredCountry.setValue(response.getCrsList().get(0).getTaxRegistredCountry().getValue());
//            taxRegistredCountry.setCode(response.getCrsList().get(0).getTaxRegistredCountry().getCode());
//            taxRegistredCountry.setSelectable(response.getCrsList().get(0).getTaxRegistredCountry().getSelectable() != null
//                    ? response.getCrsList().get(0).getTaxRegistredCountry().getSelectable().toString():"");
//            crsListItem.setIdentifier(response.getCrsList().get(0).getIdentifier());
//            crsListItem.setAction("MODIFY");
//        } else {
//            taxRegistredCountry.setValue("");
//            taxRegistredCountry.setCode("");
//            taxRegistredCountry.setSelectable("");
//            crsListItem.setAction("ADD");
//        }
//
//        crsListItem.setTaxId(tinNo);
//        crsListItem.setTaxRegistredCountry(taxRegistredCountry);
//
//        List<CrsListItem> crsListItemList = new ArrayList<>();
//        crsListItemList.add(crsListItem);
//
//        msgReq.setCrsList(crsListItemList);
//
//        AdditionalDetails additionalDetails = new AdditionalDetails();
//        additionalDetails.setSalesServiceTaxNo(sstNo);
//        msgReq.setAdditionalDetails(additionalDetails);
//
//        msgReq.setType("corporateParty");
//
//        OnboardingInformation onboardingInformation = new OnboardingInformation();
//
//        OnboardingType onboardingType = new OnboardingType();
//        onboardingType.setSelectable(response.getOnboardingInformation().getOnboardingType().getSelectable() != null ?
//                response.getOnboardingInformation().getOnboardingType().getSelectable().toString() : "");
//        onboardingType.setValue(response.getOnboardingInformation().getOnboardingType().getValue());
//        onboardingType.setCode(response.getOnboardingInformation().getOnboardingType().getCode());
//        onboardingInformation.setOnboardingType(onboardingType);
//
//        OnboardingStatus onboardingStatus = new OnboardingStatus();
//        onboardingStatus.setValue(response.getOnboardingInformation().getOnboardingStatus().getValue());
//        onboardingStatus.setSelectable(response.getOnboardingInformation().getOnboardingStatus().getSelectable() != null
//                ? response.getOnboardingInformation().getOnboardingStatus().getSelectable().toString():"");
//        onboardingStatus.setCode(response.getOnboardingInformation().getOnboardingStatus().getCode());
//        onboardingInformation.setOnboardingStatus(onboardingStatus);
//
//        ApprovalStatus approvalStatus = new ApprovalStatus();
//        approvalStatus.setValue("Pending Trigger Review CDDC Maker");
//        approvalStatus.setSelectable("true");
//        approvalStatus.setCode("P_TR_CDDC_MKR");
//        onboardingInformation.setApprovalStatus(approvalStatus);
//
//        msgReq.setOnboardingInformation(onboardingInformation);
//
//        Ucif ucif = new Ucif();
//        ucif.setCifNumber(response.getUcif().getCifNumber());
//        msgReq.setUcif(ucif);
//
//        msgReq.setPrefferedDateFormat("dd/MM/yyyy");
//
//        LocalCIF localCIF = new LocalCIF();
//        localCIF.setCifNumber(response.getLocalCIF().getCifNumber());
//        localCIF.setSourceSystem(response.getLocalCIF().getSourceSystem());
//        localCIF.setCountryOfOnboarding(response.getCountryOfOnboarding());
//        msgReq.setLocalCIF(localCIF);
//
//        Demographics demographics = new Demographics();
//        PartyClass partyClass = new PartyClass();
//        if (response.getDemographics().getPartyClass() != null){
//            partyClass.setSelectable(response.getDemographics().getPartyClass().getSelectable() != null ?response.getDemographics().getPartyClass().getSelectable().toString() : null);
//            partyClass.setCode(response.getDemographics().getPartyClass().getCode());
//            partyClass.setValue(response.getDemographics().getPartyClass().getValue());
//        } else {
//            partyClass.setSelectable("");
//            partyClass.setCode("");
//            partyClass.setValue("");
//        }
//        demographics.setPartyClass(partyClass);
//        msgReq.setDemographics(demographics);
//
//        msgReq.setCountryOfOnboarding(response.getCountryOfOnboarding());
//
//        msg.setMsgBody(msgReq);
//
//        payload.setMsg(msg);
//
//        return payload;
//    }
//
//
//}
