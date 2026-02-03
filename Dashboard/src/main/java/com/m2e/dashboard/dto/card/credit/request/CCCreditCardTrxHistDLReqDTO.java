package com.m2e.dashboard.dto.card.credit.request;

import com.m2e.dashboard.dto.base.DownloadReq;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.OffsetDateTime;
import java.util.Map;

@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class CCCreditCardTrxHistDLReqDTO extends DownloadReq {
    private String cardNumber;
    private String searchParam;
    private Map<String,Object> filters;
}
