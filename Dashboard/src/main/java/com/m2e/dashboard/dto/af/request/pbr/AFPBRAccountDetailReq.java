package com.m2e.dashboard.dto.af.request.pbr;

import com.m2e.dashboard.enums.AccountLevelEnum;
import lombok.Data;

import java.io.Serializable;

@Data
public class AFPBRAccountDetailReq implements Serializable {
    private String masterAcctNumber;
    private String acctNumber;
    private String currency;
    private AccountLevelEnum accountLevelEnum;
}
