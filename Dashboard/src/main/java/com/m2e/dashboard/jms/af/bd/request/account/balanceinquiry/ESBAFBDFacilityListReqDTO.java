package com.m2e.dashboard.jms.af.bd.request.account.balanceinquiry;

import com.m2e.dashboard.dto.af.response.bd.AFBDAcctListViewFacilityDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ESBAFBDFacilityListReqDTO {
    private String corpCifNo;
    private String acctType;
    private List<AFBDAcctListViewFacilityDTO> datas;
}
