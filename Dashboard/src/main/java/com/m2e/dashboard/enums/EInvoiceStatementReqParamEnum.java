package com.m2e.dashboard.enums;

import lombok.Getter;

@Getter
public enum EInvoiceStatementReqParamEnum {
    GCIF("gcif", "1025593"),
    ACCTNUMBER("accountNo", ""),
    SRCINVOICENUMBER("sourceInvoiceNumber", ""),
    STARTDATE("startDate", ""),
    ENDDATE("endDate", ""),
    PAGENUMBER("pageNo", ""),
    PAGESIZE("pageSize", ""),
    REQCHANNEL("reqChannel", "M2E"),
    REQLOGINID("reqLoginId", "ARINEWMAKER"),
    REQDATETIME("reqDateTime", "20250429161602"),
    REQREFID("reqReferenceId", "250429028992"),

    IDVALUE("idValue", "123456789K"),
    IDTYPE("idType", "BRN"),
    HAPIKEY("X-API-Key","a1b2c3d4e5f6g7h8i9j0"),

    LINKED("linked", "Y")
    ;
    private final String paramKey;
    private final String defaultValue;
    EInvoiceStatementReqParamEnum(String paramKey, String defaultValue) {
        this.paramKey = paramKey;
        this.defaultValue = defaultValue;
    }
}
