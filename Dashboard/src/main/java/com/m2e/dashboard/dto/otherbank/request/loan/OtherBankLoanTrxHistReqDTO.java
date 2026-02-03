package com.m2e.dashboard.dto.otherbank.request.loan;

import com.m2e.dashboard.dto.common.BasePageRequest;
import com.m2e.dashboard.dto.otherbank.request.OtherBankTrxHistoryReqDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class OtherBankLoanTrxHistReqDTO extends BasePageRequest {
    private String acctNumber;
    private String trxCode;
    private String searchParam;
    private OtherBankTrxHistoryReqDTO filters;
    private String[] headers;
    private String msgType;
}
