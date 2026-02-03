package com.m2e.dashboard.dto.otherbank.request.loan;

import com.m2e.dashboard.enums.MessageType;
import lombok.Data;

@Data
public class OtherBankLoanAcctDetailReqDTO {
    private MessageType messageType;
    private String acctNumber;
}
