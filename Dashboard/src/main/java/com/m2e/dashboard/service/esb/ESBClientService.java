//package com.m2e.dashboard.service.esb;
//
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.m2e.dashboard.config.jms.MQIntegrationProperties;
//import com.m2e.dashboard.dto.common.ApiResponse;
//import com.m2e.dashboard.dto.common.ErrorResponse;
//import com.m2e.dashboard.enums.ESBServiceType;
//import com.m2e.dashboard.enums.PropertyServiceEnum;
//import com.m2e.dashboard.jms.common.MQPropertiesDTO;
//import com.m2e.dashboard.jms.common.request.ESBReqDTO;
//import com.m2e.dashboard.jms.common.response.ESBDTO;
//import com.m2e.dashboard.service.BaseESBMetadata;
//import com.m2e.dashboard.service.esb.client.ESBClient;
//import io.github.resilience4j.timelimiter.TimeLimiter;
//import io.github.resilience4j.timelimiter.TimeLimiterRegistry;
//import jakarta.annotation.PostConstruct;
//import lombok.RequiredArgsConstructor;
//import lombok.SneakyThrows;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//import java.util.Map;
//import java.util.Objects;
//import java.util.concurrent.CompletableFuture;
//import java.util.function.Supplier;
//
//@Slf4j
//@RequiredArgsConstructor
//@Service
//public class ESBClientService {
//    private final ObjectMapper objectMapper;
//    private final List<ESBRequestHandler<?, ?>> esbServiceHandlers;
//    private final Map<ESBServiceType, ESBResponseProcessor<?>> responseProcessors;
//    private final ESBClient esbClient;
//    private final MQIntegrationProperties mqIntegrationProperties;
//    private final ESBMetadataService esbMetadataService;
//    private final TimeLimiterRegistry timeLimiterRegistry;
//    private TimeLimiter timeLimiter;
//
//    @PostConstruct
//    void init() {
//        this.timeLimiter = timeLimiterRegistry.timeLimiter("esbClientService");
//    }
//
//    @SneakyThrows
//    public <T, R extends BaseESBMetadata> ApiResponse<R> execute(T request, ESBServiceType serviceType, Supplier<R> defaultResponseSupplier, PropertyServiceEnum queueName) {
//        MQPropertiesDTO jmsProperties = mqIntegrationProperties.getIbmMq(PropertyServiceEnum.ESB.getValue());
//        String ibmMq = mqIntegrationProperties.findRequestQueue(jmsProperties, queueName);
//        log.info("Preparing call for ESB Service Type {} --> {} --> to queue {}", serviceType, request, ibmMq);
//
//        ESBRequestHandler<T, ESBReqDTO> handler = getHandlerForServiceType(serviceType);
//        ESBReqDTO esbRequest = handler.prepareRequest(request);
//        // Log the esbRequest in JSON format
//        String esbRequestJson = objectMapper.writeValueAsString(esbRequest);
//        log.info("Request payload for ESB Service Type {} --> {}", serviceType, esbRequestJson);
//
//        ApiResponse<R> apiResponse = new ApiResponse<>();
//        apiResponse.setMsgHeader(new ErrorResponse("0"));
//
//        String resp = null;
//        try {
//            long startTime = System.currentTimeMillis();
//
//            resp = timeLimiter.executeFutureSupplier(() ->
//                    CompletableFuture.supplyAsync(() -> {
//                        try {
//                            return esbClient.callHost(objectMapper.writeValueAsString(esbRequest), queueName);
//                        } catch (JsonProcessingException e) {
//                            return null;
//                        }
//                    })
//            );
//
//            long endTime = System.currentTimeMillis();
//            log.info("ESB fcall duration : {} ms", (endTime - startTime));
//
//        } catch (Exception e) {
//            log.info("fallback after 1 minute");
//            R defaultResponse = defaultResponseSupplier.get();
//            esbMetadataService.attachTimeoutMetadata(defaultResponse);
//            apiResponse.setMsgBody(defaultResponse);
//            return apiResponse;
//        }
//
//        String respQueue = mqIntegrationProperties.findResponseQueue(jmsProperties, queueName);
//        log.info("Response payload for ESB Service Type {} --> {} --> from queue {}", serviceType, resp, respQueue);
//
//        if (Objects.nonNull(resp)) {
//            ESBDTO result = objectMapper.readValue(resp, ESBDTO.class);
//            ESBResponseProcessor<R> processor = getProcessorForServiceType(serviceType);
//            R responseBody = processor.processResponse(result, request);
//
//            apiResponse.setMsgBody(responseBody);
//            return apiResponse;
//        }
//        //return default body if host response is null
//        R defaultResponse = defaultResponseSupplier.get();
//        apiResponse.setMsgBody(defaultResponse);
//
//        return apiResponse;
//    }
//
//    private <T> ESBRequestHandler<T, ESBReqDTO> getHandlerForServiceType(ESBServiceType serviceType) {
//        return (ESBRequestHandler<T, ESBReqDTO>) esbServiceHandlers.stream()
//                .filter(handler -> handler.getServiceType().equals(serviceType.name()))
//                .findFirst()
//                .orElseThrow(() -> new IllegalArgumentException("ESB Service Type is undefined"));
//    }
//
//    private <R> ESBResponseProcessor<R> getProcessorForServiceType(ESBServiceType serviceType) {
//        return (ESBResponseProcessor<R>) responseProcessors.get(serviceType);
//    }
//}
