package com.m2e.dashboard.jms.af.pbr.request.account.balanceinquiry;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ESBAFPBRUnitListReqDTO<T> {
    private String acctType;
    private List<T> datas;
}
