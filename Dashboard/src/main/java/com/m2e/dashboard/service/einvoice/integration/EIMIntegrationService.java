package com.m2e.dashboard.service.einvoice.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.m2e.dashboard.config.M2EIntegrationEIMProperties;
import com.m2e.dashboard.dto.einvoice.request.EIMUpdateLinkedStatusReqDTO;
import com.m2e.dashboard.dto.einvoice.request.EInvoiceRequestLinkedStatusReqDTO;
import com.m2e.dashboard.dto.einvoice.request.EInvoiceStatementListingReqDTO;
import com.m2e.dashboard.dto.einvoice.response.EIMRequestLinkedStatusDTO;
import com.m2e.dashboard.dto.einvoice.response.EIMStatementDTO;
import com.m2e.dashboard.dto.einvoice.response.EIMUpdatedLinkedStatusDTO;
import com.m2e.dashboard.enums.EInvoiceStatementReqParamEnum;
import com.m2e.utils.webclient.dto.APIRequestParamDTO;
import com.m2e.utils.webclient.dto.ApiRequestDTO;
import com.m2e.utils.webclient.dto.ApiResponseWrapperDTO;
import com.m2e.utils.webclient.service.APIClientService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@ConditionalOnProperty(name = "m2e.implementation", havingValue = "production", matchIfMissing = true)
public class EIMIntegrationService implements EIMIntegration{

    private static final Logger log = LoggerFactory.getLogger(EIMIntegrationService.class);

    private final M2EIntegrationEIMProperties m2EIntegrationEIMProperties;
    private final ObjectMapper objectMapper;
    private final APIClientService apiClientService;
    private static final String E_INVOICE = "einvoice";

    @Autowired
    public EIMIntegrationService(M2EIntegrationEIMProperties m2EIntegrationEIMProperties, ObjectMapper objectMapper, APIClientService apiClientService){
        this.m2EIntegrationEIMProperties = m2EIntegrationEIMProperties;
        this.objectMapper = objectMapper;
        this.apiClientService = apiClientService;
    }

    public byte[] getDownloadByteData(String url){
        if (url == null) {
            log.error("Download URL is null.");
            return new byte[0];
        }

        ApiRequestDTO<byte[]> apiReq = new ApiRequestDTO<>();
        apiReq.setBaseUrl(url);
        apiReq.setHttpMethod(HttpMethod.GET);
        apiReq.setResponseType(new ParameterizedTypeReference<>() {});

        ApiResponseWrapperDTO eimResp = null;
        try {
            if (log.isInfoEnabled()) {
                log.info("EIM download e-invoices API request --> {}", url);
            }
            eimResp = apiClientService.callHost(apiReq, null);
        } catch (Exception e) {
            log.info("calling EIM download e-invoices API error --> {}", e.getMessage(), e);
        }
        log.info("EIM download e-invoices API response --> {} ", eimResp);

        Object data = eimResp != null ? eimResp.getData() : null;
        if (data instanceof byte[] dataResult) {
            return dataResult;
        } else if (data instanceof String dataResult) {
            return Base64.getDecoder().decode(dataResult);
        } else {
            return objectMapper.convertValue(data, byte[].class);
        }
    }

    public EIMRequestLinkedStatusDTO requestLinkedStatusDTO (String userId, EInvoiceRequestLinkedStatusReqDTO data){
        // curl --insecure 'https://172.31.196.126:8445/eim-ws/api/v1/einvoice/1025593/link-status?gcif=1025593&idValue=123456789K&idType=BRN&reqChannel=M2E&reqLoginId=ARINEWMAKER&reqReferenceId=250408028738&reqDateTime=20250123070707' --header "X-API-Key: a1b2c3d4e5f6g7h8i9j0" --header "Content-Type: application/json"
        String eimURL = m2EIntegrationEIMProperties.getUrl();
        String eimEInvoiceRequestLinkedStatusURI = m2EIntegrationEIMProperties.getUri(E_INVOICE, "linkStatus");
        if (eimURL == null || eimEInvoiceRequestLinkedStatusURI == null) {
            log.error("EIM URL or e-Invoice request linked status URI is null.");
            return null;
        }

        ApiRequestDTO<EIMRequestLinkedStatusDTO> apiReq = new ApiRequestDTO<>();
        apiReq.setBaseUrl(eimURL);
        apiReq.setUri(eimEInvoiceRequestLinkedStatusURI);
        apiReq.setHttpMethod(HttpMethod.GET);
        apiReq.setUriVariables(new String[]{data.getCifNo()});
        apiReq.setRequestParams(Arrays.asList(
                new APIRequestParamDTO(EInvoiceStatementReqParamEnum.GCIF.getParamKey(), data.getCifNo()),
                // Removed due to ticket M2UBIZM25-64143
                // new APIRequestParamDTO(EInvoiceStatementReqParamEnum.IDVALUE.getParamKey(), data.getBrn()),
                new APIRequestParamDTO(EInvoiceStatementReqParamEnum.IDTYPE.getParamKey(), EInvoiceStatementReqParamEnum.IDTYPE.getDefaultValue()),
                new APIRequestParamDTO(EInvoiceStatementReqParamEnum.REQCHANNEL.getParamKey(), EInvoiceStatementReqParamEnum.REQCHANNEL.getDefaultValue()),
                new APIRequestParamDTO(EInvoiceStatementReqParamEnum.REQLOGINID.getParamKey(), userId),
                new APIRequestParamDTO(EInvoiceStatementReqParamEnum.REQDATETIME.getParamKey(), LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))),
                new APIRequestParamDTO(EInvoiceStatementReqParamEnum.REQREFID.getParamKey(), String.valueOf(System.currentTimeMillis()))));
        apiReq.setResponseType(new ParameterizedTypeReference<>() {});
        apiReq.setHeaders(buildEimHeaders());
        ApiResponseWrapperDTO eimResp = callEim(apiReq, "EIM e-invoices request linked status API request");
        log.info("EIM e-invoices request linked status API response --> {} ", eimResp);
        return eimResp != null ? objectMapper.convertValue(eimResp.getData(), EIMRequestLinkedStatusDTO.class) : null;
    }

    public EIMUpdatedLinkedStatusDTO updatedEInvoiceLinkedStatus(EIMUpdateLinkedStatusReqDTO data) {
        // curl --insecure -X POST https://172.31.196.126:8445/eim-ws/api/v1/einvoice/update-link-status -d '{"gcif":"1025593","linked":"Y","idValue":"123456789K","reqChannel":"M2E","reqLoginId":"ARINEWMAKER","reqDateTime":"20250123070707","reqReferenceId":"250408028738"}' --header "X-API-Key: a1b2c3d4e5f6g7h8i9j0" --header "Content-Type: application/json"
        String eimURL = m2EIntegrationEIMProperties.getUrl();
        String eimEInvoiceUpdateLinkedStatusURI = m2EIntegrationEIMProperties.getUri(E_INVOICE, "updateLinkStatus");
        if (eimURL == null || eimEInvoiceUpdateLinkedStatusURI == null) {
            log.error("EIM URL or e-Invoice update linked status URI is null.");
            return null;
        }

        ApiRequestDTO<EIMUpdatedLinkedStatusDTO> apiReq = new ApiRequestDTO<>();
        apiReq.setBaseUrl(eimURL);
        apiReq.setUri(eimEInvoiceUpdateLinkedStatusURI);
        apiReq.setHttpMethod(HttpMethod.POST);
        apiReq.setBody(data);
        apiReq.setResponseType(new ParameterizedTypeReference<>() {});
        apiReq.setHeaders(buildEimHeaders());
        ApiResponseWrapperDTO eimResp = null;
        try {
            if (log.isInfoEnabled()) {
                log.info("sentToEIM for update linked status request --> {}", objectMapper.writeValueAsString(apiReq));
            }
            eimResp = apiClientService.callHost(apiReq, null);
            if (log.isInfoEnabled()) {
                log.info("sentToEIM for update linked status response --> {}", objectMapper.writeValueAsString(eimResp));
            }
        } catch (Exception e) {
            log.error("sentToEIM for update linked status error --> {}", e.getMessage(), e);
        }

        return eimResp != null ? objectMapper.convertValue(eimResp.getData(), EIMUpdatedLinkedStatusDTO.class) : null;
    }

    public EIMStatementDTO getListEInvoice(String userId, EInvoiceStatementListingReqDTO req) {
        // curl --insecure 'https://172.31.196.126:8445/eim-ws/api/v1/einvoice/list-e-invoice?accountNo=000712361000020&sourceInvoiceNumber&startDate&endDate&pageNo=1&pageSize=10&reqChannel=M2E&reqLoginId=ARINEWMAKER&reqDateTime=20250429161602&reqReferenceId=250429028992' --header "X-API-Key: a1b2c3d4e5f6g7h8i9j0" --header "Content-Type: application/json"
        String eimURL = m2EIntegrationEIMProperties.getUrl();
        String eimEInvoiceListURI = m2EIntegrationEIMProperties.getUri(E_INVOICE, "list");
        if (eimURL == null || eimEInvoiceListURI == null) {
            log.error("EIM URL or e-Invoice List URI is null.");
            return null;
        }

        ApiRequestDTO<EIMStatementDTO> apiReq = new ApiRequestDTO<>();
        apiReq.setBaseUrl(eimURL);
        apiReq.setUri(eimEInvoiceListURI);
        apiReq.setHttpMethod(HttpMethod.GET);
        List<APIRequestParamDTO> requestParams = new ArrayList<>(List.of(
                new APIRequestParamDTO(EInvoiceStatementReqParamEnum.ACCTNUMBER.getParamKey(), padWithLeadingZeros(req.getAcctNumber())),
                new APIRequestParamDTO(EInvoiceStatementReqParamEnum.PAGESIZE.getParamKey(), String.valueOf(req.getPageSize())),
                new APIRequestParamDTO(EInvoiceStatementReqParamEnum.PAGENUMBER.getParamKey(), String.valueOf(req.getPage())),
                new APIRequestParamDTO(EInvoiceStatementReqParamEnum.REQCHANNEL.getParamKey(), EInvoiceStatementReqParamEnum.REQCHANNEL.getDefaultValue()),
                new APIRequestParamDTO(EInvoiceStatementReqParamEnum.REQLOGINID.getParamKey(), userId),
                new APIRequestParamDTO(EInvoiceStatementReqParamEnum.REQDATETIME.getParamKey(), LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))),
                new APIRequestParamDTO(EInvoiceStatementReqParamEnum.REQREFID.getParamKey(), String.valueOf(System.currentTimeMillis()))
        ));
        log.info("getListEInvoice startDate -> {}", req.getStartDate());
        Optional.ofNullable(req.getStartDate())
                .ifPresent(start -> requestParams.add(new APIRequestParamDTO(EInvoiceStatementReqParamEnum.STARTDATE.getParamKey(), start)));
        log.info("getListEInvoice endDate -> {}", req.getEndDate());
        Optional.ofNullable(req.getEndDate())
                .ifPresent(end -> requestParams.add(new APIRequestParamDTO(EInvoiceStatementReqParamEnum.ENDDATE.getParamKey(), end)));

        apiReq.setRequestParams(requestParams);
        apiReq.setResponseType(new ParameterizedTypeReference<>() {});
        apiReq.setHeaders(buildEimHeaders());
        ApiResponseWrapperDTO eimResp = callEim(apiReq, "EIM list e-invoices API request");
        log.info("EIM list e-invoices API response --> {} ", eimResp);
        return eimResp != null ? objectMapper.convertValue(eimResp.getData(), EIMStatementDTO.class) : null;
    }

    private Map<String, String> buildEimHeaders() {
        Map<String, String> headers = new HashMap<>();
        headers.put(EInvoiceStatementReqParamEnum.HAPIKEY.getParamKey(), m2EIntegrationEIMProperties.getApiKey());
        return headers;
    }

    private ApiResponseWrapperDTO callEim(ApiRequestDTO<?> apiReq,
                                                           String actionDescription) {
        ApiResponseWrapperDTO eimResp = null;
        try {
            if (log.isInfoEnabled()) {
                log.info("{} --> {}", actionDescription, objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(apiReq));
            }
            eimResp = apiClientService.callHost(apiReq, null);
        } catch (Exception e) {
            log.info("{} error --> {}", actionDescription, e.getMessage(), e);
        }
        return eimResp;
    }

    public static String padWithLeadingZeros(String acctNo) {
        if (acctNo == null) {
            acctNo = "";
        }
        return String.format("%015d", Long.parseLong(acctNo));
    }
}