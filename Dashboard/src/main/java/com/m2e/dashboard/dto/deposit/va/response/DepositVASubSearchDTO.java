package com.m2e.dashboard.dto.deposit.va.response;

import lombok.Data;

import java.util.List;


@Data
public class DepositVASubSearchDTO {
    private String currAcctNumber;
    private String currAcctName;
    private Integer totVa;
    private List<DepositVASearchDto> virtualAccount;

    public DepositVASubSearchDTO(String currAcctNumber, String currAcctName, Integer totVa, List<DepositVASearchDto> virtualAccount) {
        this.currAcctNumber = currAcctNumber;
        this.currAcctName = currAcctName;
        this.totVa = totVa;
        this.virtualAccount = virtualAccount;
    }
}
