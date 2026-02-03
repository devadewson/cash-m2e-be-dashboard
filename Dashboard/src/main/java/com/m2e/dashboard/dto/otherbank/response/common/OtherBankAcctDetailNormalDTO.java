package com.m2e.dashboard.dto.otherbank.response.common;

import com.m2e.dashboard.dto.otherbank.response.common.OtherBankAcctDetailDTO;
import lombok.Data;

@Data
public class OtherBankAcctDetailNormalDTO extends OtherBankAcctDetailDTO {
    private String statementReference;
    private String statementNumber;

    private String statementDate;
}
