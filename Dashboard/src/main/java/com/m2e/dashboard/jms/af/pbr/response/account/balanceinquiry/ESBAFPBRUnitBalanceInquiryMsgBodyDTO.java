package com.m2e.dashboard.jms.af.pbr.response.account.balanceinquiry;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.m2e.dashboard.jms.af.pbr.response.account.details.ESBAFPBRUnitDTO;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class ESBAFPBRUnitBalanceInquiryMsgBodyDTO implements Serializable {
    @JsonProperty("Account")
    private List<ESBAFPBRUnitDTO> accounts;
}
