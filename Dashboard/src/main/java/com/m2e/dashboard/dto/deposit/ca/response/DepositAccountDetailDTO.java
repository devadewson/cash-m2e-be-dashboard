package com.m2e.dashboard.dto.deposit.ca.response;

import com.m2e.dashboard.jms.common.response.ESBMetadataDTO;
import com.m2e.dashboard.service.BaseESBMetadata;
import lombok.Data;

@Data
public class DepositAccountDetailDTO implements BaseESBMetadata {
    private String acctName;
    private String companyName; // TODO - Not company name, but full product name
    private String acctNumber;
    private String currency;
    private String lastUpdatedDateTime;
    private String ledgerBalance;
    private String availableBalance;
    private String actualBalance;
    private String productName;
    private String acctType;
    private String acctTypeDesc;
    private String holdAmount;
    private String odLimit;
    private String utilisedOD;
    private String unUtilisedOD;
    private String authODLimit;
    private String intraDayODLimit;
    private String unUtilisedIntraDayOD;
    private String omnibusODLimit;
    private String unUtilisedOmnibusOD;
    private String oneDayFloat;
    private String oneDayFloatExpiryDate;
    private String twoDayFloat;
    private String twoDayFloatExpiryDate;
    private String otherDayFloat;
    private String totalFloat;
    private ESBMetadataDTO esbMetadata;
    private String acctStatusDesc;
    private String isIslamic;
}
