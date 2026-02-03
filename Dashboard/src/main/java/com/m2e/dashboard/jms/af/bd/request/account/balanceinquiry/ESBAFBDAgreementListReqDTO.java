package com.m2e.dashboard.jms.af.bd.request.account.balanceinquiry;

import com.m2e.dashboard.dto.af.response.bd.AFBDSubChildAcctListDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ESBAFBDAgreementListReqDTO {
    private String provider;
    private String masterAccountNumber;
    private String blockNumber;
    private String acctType;
    private List<AFBDSubChildAcctListDTO> datas;
}
