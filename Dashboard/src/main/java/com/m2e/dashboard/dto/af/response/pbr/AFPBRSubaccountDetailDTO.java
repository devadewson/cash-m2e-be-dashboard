package com.m2e.dashboard.dto.af.response.pbr;

import com.m2e.dashboard.dto.MoreActionsEntitlement;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class AFPBRSubaccountDetailDTO extends AFPBRAccountDetail {
    private String unitNumber;
    private String financedAmount;
    private String settlementAmount;
    private String purchasedPrice;
    private String amountDue;
    private String penaltyAmount;
    private String outstandingBalance;
    private String engineNumber;
    private String chassisNumber;
    private String expiryDate;
    private String unitStatus;
    private String dateFinanced;
    private String acctStatusDesc;
    private MoreActionsEntitlement moreActions;

}
