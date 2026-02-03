package com.m2e.dashboard.jms.af.bd.request.account.balanceinquiry;

import com.m2e.dashboard.dto.af.response.bd.AFBDAcctListViewAcctDTO;
import com.m2e.dashboard.dto.common.SearchCollectionResponse;
import com.m2e.dashboard.sp.deposit.ca.response.DepositAcctSummarySpDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ESBAFBDAcctListReqDTO {
    private String corpCifNo;
    private String acctType;
    private List<AFBDAcctListViewAcctDTO> datas;
}
