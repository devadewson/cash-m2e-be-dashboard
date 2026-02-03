package com.m2e.dashboard.dto.card.charge.request;

import com.m2e.dashboard.dto.common.BasePageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.OffsetDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
public class CCChargeCardTrxHistReqDTO extends BasePageRequest {
    private String cardNumber;
    private String searchParam;
    private CCChargeCardTrxHistFilterReqDTO filters;
}
