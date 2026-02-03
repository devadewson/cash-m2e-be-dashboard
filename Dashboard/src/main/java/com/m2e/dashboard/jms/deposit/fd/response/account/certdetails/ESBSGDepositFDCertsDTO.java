package com.m2e.dashboard.jms.deposit.fd.response.account.certdetails;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.m2e.dashboard.jms.common.response.ESBDetailDTO;
import com.m2e.dashboard.jms.common.response.ESBDetailDTOAware;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class ESBSGDepositFDCertsDTO implements ESBDetailDTOAware, Serializable {
    @JsonProperty("AccountNo")
    private String accountNumber;
    @JsonProperty("Currency")
    private String currency;
    @JsonProperty("Region")
    private String region;
    @JsonProperty("Details")
    private List<ESBDetailDTO> details;
}
