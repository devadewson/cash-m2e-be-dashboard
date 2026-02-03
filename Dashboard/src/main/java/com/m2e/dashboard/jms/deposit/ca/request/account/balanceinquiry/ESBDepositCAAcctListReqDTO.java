package com.m2e.dashboard.jms.deposit.ca.request.account.balanceinquiry;

import com.m2e.dashboard.config.UserAndCorp;
import com.m2e.dashboard.dto.common.SearchCollectionResponse;
import com.m2e.dashboard.sp.deposit.ca.response.DepositAcctSummarySpDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ESBDepositCAAcctListReqDTO {
    private String corpCifNo;
    private String corpProvider;
    private SearchCollectionResponse<DepositAcctSummarySpDTO> datas;
    private UserAndCorp userAndCorp;
}
