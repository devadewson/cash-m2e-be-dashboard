package com.m2e.dashboard.jms.af.bd.request.account.balanceinquiry;

import com.m2e.dashboard.dto.af.response.bd.AFBDAcctListViewAcctDTO;
import com.m2e.dashboard.dto.af.response.bd.AFBDSubAcctListSGDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ESBAFBDSubAcctListSGReqDTO {
    private String provider;
    private String masterAccountNumber;
    private String acctType;
    private List<AFBDSubAcctListSGDTO> datas;
}
