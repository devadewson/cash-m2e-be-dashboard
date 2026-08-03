package com.m2e.dashboard.service;

import com.m2e.dashboard.config.UserAndCorp;
import com.m2e.dashboard.dto.base.*;
import com.m2e.dashboard.dto.common.ApiResponse;
import com.m2e.dashboard.dto.common.ErrorResponse;
import com.m2e.dashboard.dto.common.PaymentTypeDTO;
import com.m2e.dashboard.dto.common.PaymentTypeReq;
import com.m2e.dashboard.projection.PaymentTypeProjection;
import com.m2e.dashboard.repository.PaymentTypeRepository;
import com.m2e.dashboard.sp.common.request.SearchResultCountAFSpParam;
import com.m2e.dashboard.sp.common.request.SearchResultCountSpParam;
import com.m2e.dashboard.util.UtilityFunctions;
import com.m2e.utils.jdbc.model.dto.BaseSpDto;
import com.m2e.utils.jdbc.service.BaseSpService;
import com.m2e.utils.redis.helper.RedisHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static com.m2e.dashboard.enums.SPEnum.*;

@Service
@Slf4j
public class CommonService {
    private final BaseSpService baseSpService;
    @Autowired(required = false)
    private  RedisHelper redisHelper;
    private final UtilityFunctions utilityFunctions;
    private final PaymentTypeRepository paymentTypeRepository;

    @Autowired
    public CommonService(BaseSpService baseSpService, UtilityFunctions utilityFunctions, PaymentTypeRepository paymentTypeRepository){
        this.baseSpService = baseSpService;
//        this.redisHelper = redisHelper;
        this.utilityFunctions = utilityFunctions;
        this.paymentTypeRepository = paymentTypeRepository;
    }

    public ApiResponse<SearchResultCountDTO> getSearchResultCount(UserAndCorp userAndCorp, SearchResultCountReqDTO req) {
        switch (req.getProduct()) {
            case "TRADE":
                return getSearchResultCountSp(userAndCorp, req, SP_TD_SEARCH_COUNTER.getValue());
            case "DEPOSIT":
                if ("CA".equals(req.getSubProduct())) {
                    return getSearchResultCountSp(userAndCorp, req, SP_DP_CA_SEARCH_COUNTER.getValue());
                } else if ("VA".equals(req.getSubProduct())) {
                    return getSearchResultCountSp(userAndCorp, req, SP_DP_VA_SEARCH_COUNTER.getValue());
                }else {
                    return getSearchResultCountSp(userAndCorp, req, SP_DP_FD_SEARCH_COUNTER.getValue());
                }
            case "LOAN":
                if ("TL".equals(req.getSubProduct())) {
                    return getSearchResultCountSp(userAndCorp, req, SP_LN_TL_SEARCH_COUNTER.getValue());
                } else if ("RC".equals(req.getSubProduct())) {
                    return getSearchResultCountSp(userAndCorp, req, SP_LN_RC_SEARCH_COUNTER.getValue());
                } else { // OD
                    return getSearchResultCountSp(userAndCorp, req, SP_LN_OD_SEARCH_COUNTER.getValue());
                }
            case "AUTOFINANCE":
                if ("FS".equals(req.getSubProduct())) {
                    return getAutoFinanceSearchResultCountSp(userAndCorp, req, SP_AF_FS_SEARCH_COUNTER.getValue());
                } else if ("HP".equals(req.getSubProduct())) {
                    return getAutoFinanceSearchResultCountSp(userAndCorp, req, SP_AF_HP_SEARCH_COUNTER.getValue());
                } else if ("PBR".equals(req.getSubProduct())) {
                    return getAutoFinanceSearchResultCountSp(userAndCorp, req, SP_AF_PBR_SEARCH_COUNTER.getValue());
                } else if ("BD".equals(req.getSubProduct())) {
                    return getAutoFinanceSearchResultCountSp(userAndCorp, req, SP_AF_BD_SEARCH_COUNTER.getValue());
                } else {
                    return null;
                }
            case "OTHERBANK":
                return getSearchResultCountSp(userAndCorp, req, SP_OB_SEARCH_COUNTER.getValue());
            case "INVESTMENT":
                return getSearchResultCountSp(userAndCorp, req, SP_IV_UT_SEARCH_COUNTER.getValue());
            default:
                return null;
        }
    }

    public ApiResponse<SearchResultCountDTO> getSearchResultCountSp(UserAndCorp userAndCorp, SearchResultCountReqDTO req, String spId) {
        SearchResultCountSpParam searchResultCountSpParam = new SearchResultCountSpParam();
        searchResultCountSpParam.setCorpId(userAndCorp.getCorpId());
        searchResultCountSpParam.setUserId(userAndCorp.getUserId());
        searchResultCountSpParam.setSearchParam(req.getSearchParam());
        searchResultCountSpParam.setSubProduct(req.getSubProduct());
        searchResultCountSpParam.setCorpCifNo(req.getCorpCifNo());

        BaseSpDto<SearchResultCountSpParam, SearchResultCountDTO> baseSpDto = new BaseSpDto<>();
        baseSpDto.setSpId(spId);
        baseSpDto.setParameter(searchResultCountSpParam);
        baseSpDto.setResultClass(SearchResultCountDTO.class);
        BaseSpDto<SearchResultCountSpParam, SearchResultCountDTO> resultBaseSpDto = baseSpService.execute(baseSpDto);
        List<SearchResultCountDTO> spResultList = Optional.ofNullable(resultBaseSpDto)
                .map(BaseSpDto::getResults)
                .orElse(Collections.emptyList());
        SearchResultCountDTO result = spResultList.stream().findFirst().orElse(null);

        ApiResponse<SearchResultCountDTO> apiResponse = new ApiResponse<>();
        apiResponse.setMsgHeader(new ErrorResponse("0"));
        apiResponse.setMsgBody(result);

        return apiResponse;
    }

    public ApiResponse<SearchResultCountDTO> getAutoFinanceSearchResultCountSp(UserAndCorp userAndCorp, SearchResultCountReqDTO request, String spId) {
        SearchResultCountAFReqDTO req = (SearchResultCountAFReqDTO) request;
        SearchResultCountAFSpParam searchResultCountSpParam = new SearchResultCountAFSpParam();
        searchResultCountSpParam.setCorpId(userAndCorp.getCorpId());
        searchResultCountSpParam.setUserId(userAndCorp.getUserId());
        searchResultCountSpParam.setSearchParam(req.getSearchParam());
        searchResultCountSpParam.setSubProduct(req.getSubProduct());
        searchResultCountSpParam.setViewByIndicator(req.getViewByIndicator().getFilterBy());
        searchResultCountSpParam.setCorpCifNo(request.getCorpCifNo());

        BaseSpDto<SearchResultCountAFSpParam, SearchResultCountDTO> baseSpDto = new BaseSpDto<>();
        baseSpDto.setSpId(spId);
        baseSpDto.setParameter(searchResultCountSpParam);
        baseSpDto.setResultClass(SearchResultCountDTO.class);
        BaseSpDto<SearchResultCountAFSpParam, SearchResultCountDTO> resultBaseSpDto = baseSpService.execute(baseSpDto);
        List<SearchResultCountDTO> spResultList = Optional.ofNullable(resultBaseSpDto)
                .map(BaseSpDto::getResults)
                .orElse(Collections.emptyList());
        SearchResultCountDTO result = spResultList.stream().findFirst().orElse(null);

        ApiResponse<SearchResultCountDTO> apiResponse = new ApiResponse<>();
        apiResponse.setMsgHeader(new ErrorResponse("0"));
        apiResponse.setMsgBody(result);

        return apiResponse;
    }

    public ApiResponse<CurrencyConversionDTO> convertCurrency(CurrencyConversionReqDTO req) {
        List<String> results = req.getData().stream()
                .map(data -> convert(new BigDecimal(data.getAmount()), new BigDecimal(data.getBuyRate()), Integer.parseInt(data.getConversionUnit())))
                .map(BigDecimal::toPlainString)
                .toList();

        CurrencyConversionDTO data = new CurrencyConversionDTO();
        data.setTargetCurrency(req.getTargetCurrency());
        data.setConvertedValues(results);

        ApiResponse<CurrencyConversionDTO> apiResponse = new ApiResponse<>();
        apiResponse.setMsgHeader(new ErrorResponse("0"));
        apiResponse.setMsgBody(data);
        return  apiResponse;
    }

    public BigDecimal convert(BigDecimal amount, BigDecimal buyRate, int conversionUnit) {
        if (amount == null || buyRate == null || buyRate.compareTo(BigDecimal.ZERO) == 0) {
            throw new IllegalArgumentException("Invalid amount or buyRate");
        }

        BigDecimal conversionUnitBD = BigDecimal.valueOf(conversionUnit);

        return amount.divide(buyRate, 20, RoundingMode.HALF_UP)
                .multiply(conversionUnitBD)
                .setScale(2, RoundingMode.HALF_UP);
    }

    public ApiResponse<List<PaymentTypeDTO>> getPaymentTypeDropList(PaymentTypeReq paymentTypeReq) {
        String cacheKey = "PaymentListDropDown";
        String redisKey = redisHelper.createCacheKey(cacheKey, paymentTypeReq.getProvider());

        long minutesUntil5AM = utilityFunctions.minutesUntilNextTime(LocalTime.of(5, 0), ZoneId.of("Asia/Singapore"));

        List<PaymentTypeDTO> paymentTypeDTOList = redisHelper.getListOrLoad(
                redisKey,
                PaymentTypeDTO.class,
                () -> {
                    log.info("Cache for Payment Type Drop List: {}", redisKey);
                    return getPaymentTypeDropListFromDb(paymentTypeReq.getProvider());
                },
                minutesUntil5AM,
                TimeUnit.MINUTES
        );

        ApiResponse<List<PaymentTypeDTO>> apiResponse = new ApiResponse<>();
        apiResponse.setMsgHeader(new ErrorResponse("0"));
        apiResponse.setMsgBody(paymentTypeDTOList);

        return apiResponse;
    }

    public List<PaymentTypeDTO> getPaymentTypeDropListFromDb(String provider) {
        List<PaymentTypeProjection> paymentTypeProjections = paymentTypeRepository.findByProvider(provider);

        return paymentTypeProjections.stream()
                .map(paymentTypeProjection -> new PaymentTypeDTO(
                        paymentTypeProjection.getCode(),
                        paymentTypeProjection.getDescription(),
                        paymentTypeProjection.getProvider()
                        ))
                .toList();
    }
}
