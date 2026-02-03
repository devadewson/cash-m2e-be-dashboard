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
public class AFPBRMasterAccountDetailDTO extends AFPBRAccountDetail {
    private String facilityNumber;
    private String facilityLimit;
    private String totalFinancedAmount;
    private String totalSettlementAmount;
    private String accountType;
    private String numberOfAccounts;
    private String consolidatedUnutilisedAmount;
    private String totalPenalty;
    private String totalUnitExpired;
    private String dateApproved;
    private String pbrStatus;
    private String acctStatusDesc;
    private MoreActionsEntitlement moreActions;
}
