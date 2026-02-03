package com.m2e.dashboard.jms.deposit.fd.response.account.certdetails;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class ESBSGDepositFDCertDTO implements Serializable {
    @JsonProperty("MbbLastKey")
    private String mbbLastKey;
    @JsonProperty("Account")
    private List<ESBSGDepositFDCertsDTO> accounts;
}
