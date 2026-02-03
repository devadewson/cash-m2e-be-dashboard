package com.m2e.dashboard.enums;

import lombok.Getter;

@Getter
public enum AdminEventEnum {

    MAKER_ADM_INIT("MAKER_ADM_INIT", "TRANS_INITATED_ADM_MKER_GB", "REVIEW_TRANS_ALL_ADM_CHK_GB"),
    MAKER_ADM_APPRVD("MAKER_ADM_APPRVD", "TRANS_APPR_ADM_MKER_ALL_ADM_CHK_GB"),
    CHK_ADM_APPROVED("CHK_ADM_APPROVED", "TRANS_APPR_ADM_MKER_ALL_ADM_CHK_GB"),
    CHK_ADM_REJECTED("CHK_ADM_REJECTED", "TRANS_REJECT_ADM_MKER_ALL_ADM_CHK_GB"),
    USER_INIT_APPR("USER_INIT_APPR", "TRANS_APPR_ADM_MKER_ALL_ADM_CHK_NO_REF_GB");


    private final String eventAction;
    private final String [] templateCode;

    AdminEventEnum(String eventAction, String... templateCode) {
        this.eventAction = eventAction;
        this.templateCode = templateCode;
    }
}
