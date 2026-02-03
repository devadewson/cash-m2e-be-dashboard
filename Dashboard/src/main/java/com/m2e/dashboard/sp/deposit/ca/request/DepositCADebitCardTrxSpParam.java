package com.m2e.dashboard.sp.deposit.ca.request;

import com.m2e.dashboard.dto.common.BasePageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class DepositCADebitCardTrxSpParam extends BasePageRequest {
    private String acctNumber;
    private String cardNumber;
    private String acctCurrency;
    private String startDate;
    private String startTime;
    private String endDate;
    private String endTime;
    private Double amtFrom;
    private Double amtTo;
    private String userId;
    private String corpId;
}
