package com.m2e.dashboard.jms.af.hp.request.account.balanceinquiry;

import com.m2e.dashboard.dto.af.response.bd.AFBDAcctListViewAcctDTO;
import com.m2e.dashboard.dto.af.response.hp.AFHPAcctListDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ESBAFHPAcctListReqDTO {
    private String acctType;
    private List<AFHPAcctListDTO> datas;
}
