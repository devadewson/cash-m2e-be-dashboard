package com.m2e.dashboard.jms.af.fs.response.account.balanceinquiry;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.m2e.dashboard.jms.af.fs.response.account.details.ESBAFFSAccountDetailsDTO;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class ESBAFFSAcctInquiryMsgBodyDTO implements Serializable {
    @JsonProperty("Account")
    private List<ESBAFFSAccountDetailsDTO> accounts;
}
