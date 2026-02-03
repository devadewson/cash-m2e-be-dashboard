package com.m2e.dashboard.jms.loan.tlf.response.tranch.details;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.m2e.dashboard.jms.loan.tlf.response.account.details.ESBLoanTLFDetailDTO;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class ESBLoanTLFTranchesTrnRecDTO implements Serializable {
    @JsonProperty("Details")
    private List<ESBLoanTLFDetailDTO> details;
}
