package com.m2e.dashboard.sp.otherbank.request;

import com.m2e.dashboard.enums.MessageType;
import lombok.Data;

@Data
public class OtherBankDetailSpParam {
    private String acctNumber;
    private String userId;
    private String corpId;
    private MessageType messageType;
}
