package com.m2e.dashboard.dto.loan.tlf.response;

import com.m2e.dashboard.dto.MoreActionsEntitlement;
import com.m2e.dashboard.dto.loan.tlf.LNTLFAccountType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class LNTLFDetailDTO {
    private LNTLFAccountType accountTypeIndicators;
    private String acctName;
    private String acctNumber;
    private String currency;
    private String productCode;
    private String acctStatusCode;
    private String acctStatusDesc;
    private String accountOwnerCompany;
    private String lastUpdated; // format: 01/02/2024. HH:mm:ss (GMT+8)
    private String approvedAmount;
    private String productName;
    private String drawdownAmount;
    @Schema(name = "Outstanding *Balance*")
    private String outstandingBalanceAmount;
    private boolean hasSellingPrice = false;
    private MoreActionsEntitlement moreActions;
}
