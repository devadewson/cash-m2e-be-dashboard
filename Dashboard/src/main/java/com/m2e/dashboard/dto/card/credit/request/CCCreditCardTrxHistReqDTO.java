package com.m2e.dashboard.dto.card.credit.request;

import com.m2e.dashboard.dto.common.BasePageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.OffsetDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
public class CCCreditCardTrxHistReqDTO extends BasePageRequest {
    private String cardNumber;
    private String searchParam;
    private CCCreditCardTrxHistFilterReqDTO filters;
}
