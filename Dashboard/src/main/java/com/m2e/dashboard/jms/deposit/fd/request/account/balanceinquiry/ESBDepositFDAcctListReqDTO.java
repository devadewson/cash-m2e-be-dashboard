package com.m2e.dashboard.jms.deposit.fd.request.account.balanceinquiry;

import com.m2e.dashboard.dto.common.SearchCollectionResponse;
import com.m2e.dashboard.sp.deposit.fd.response.DepositFDAcctSummarySpDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ESBDepositFDAcctListReqDTO {
    private String corpCifNo;
    private String corpProvider;
    private SearchCollectionResponse<DepositFDAcctSummarySpDTO> datas;
}
