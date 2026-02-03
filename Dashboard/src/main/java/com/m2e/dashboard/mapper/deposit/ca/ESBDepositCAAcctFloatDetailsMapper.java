package com.m2e.dashboard.mapper.deposit.ca;

import com.m2e.dashboard.dto.deposit.ca.response.DepositAcctFloatDtlDTO;
import com.m2e.dashboard.jms.deposit.ca.response.account.details.ESBDepositCAAccountFloatDetailTrnRecDTO;
import com.m2e.dashboard.util.DateTimeUtil;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;

@Mapper(componentModel = "spring")
public interface ESBDepositCAAcctFloatDetailsMapper {
    static final Logger log = LoggerFactory.getLogger(ESBDepositCAAcctFloatDetailsMapper.class);

    ESBDepositCAAcctFloatDetailsMapper INSTANCE = Mappers.getMapper(ESBDepositCAAcctFloatDetailsMapper.class);

    @Mapping(source = "floatExpiryDate", target = "expiryDate", qualifiedByName = "formatDate")
    @Mapping(source = "transactionCode", target = "trxnCode")
    @Mapping(source = "transactionDesc", target = "trxnDesc")
    @Mapping(source = "currency", target = "currency")
    @Mapping(source = "floatAmount", target = "floatAmount")
    DepositAcctFloatDtlDTO mapToDepositAccountFloatDetailResp(ESBDepositCAAccountFloatDetailTrnRecDTO dto);

    @Named("formatDate")
    static String formatDate(String date) {
        try {
            String parsedDate = DateTimeUtil.formatESBFloatExpiryDate(date);
            return parsedDate;
        } catch (ParseException e) {
            log.error("Error formatting expiry date for {}", date, e);
            return null;
        }
    }
}
