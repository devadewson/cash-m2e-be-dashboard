package com.m2e.dashboard.dto.otherbank.response.deposit;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serializable;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OtherBankDepositAcctStatsDTO implements Serializable {
    private String noOfAccount;
    private String lastUpdatedDateTime;
}
