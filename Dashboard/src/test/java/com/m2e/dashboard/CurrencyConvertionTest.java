package com.m2e.dashboard;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.m2e.dashboard.dto.base.ConversionDataReqDTO;
import com.m2e.dashboard.dto.base.CurrencyConversionReqDTO;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CurrencyConvertionTest {

    public static void main(String[] args) throws JsonProcessingException {
        CurrencyConversionReqDTO req = new CurrencyConversionReqDTO();
        req.setTargetCurrency("CHF");

        ConversionDataReqDTO dataReq = new ConversionDataReqDTO();
        dataReq.setAmount("364310638134709900.00");
        dataReq.setConversionUnit("100");
        dataReq.setBuyRate("148.4");
        req.setData(new ArrayList<>(Arrays.asList(dataReq)));

        ObjectMapper mapper = new ObjectMapper();
        System.out.println("result -> " + mapper.writeValueAsString(convertCurrency(req)));
    }

    public static List<String> convertCurrency(CurrencyConversionReqDTO req) {
        List<String> results = req.getData().stream()
                .map(data -> convert(new BigDecimal(data.getAmount()), new BigDecimal(data.getBuyRate()), Integer.parseInt(data.getConversionUnit())))
                .map(BigDecimal::toPlainString)
                .toList();
        return  results;
    }

    public static BigDecimal convert(BigDecimal amount, BigDecimal buyRate, int conversionUnit) {
        if (amount == null || buyRate == null || buyRate.compareTo(BigDecimal.ZERO) == 0) {
            throw new IllegalArgumentException("Invalid amount or buyRate");
        }

        BigDecimal conversionUnitBD = BigDecimal.valueOf(conversionUnit);

        return amount.divide(buyRate, 20, RoundingMode.HALF_UP)
                .multiply(conversionUnitBD)
                .setScale(2, RoundingMode.HALF_UP);
    }
}
