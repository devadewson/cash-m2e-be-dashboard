package com.m2e.dashboard.jms.af.bd.request.account.balanceinquiry;

import com.m2e.dashboard.dto.af.response.bd.AFBDSubMasterAcctListDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ESBAFBDBlockListReqDTO {
    private String provider;
    private String masterAccountNumber;
    private String acctType;
    private List<AFBDSubMasterAcctListDTO> datas;
}
