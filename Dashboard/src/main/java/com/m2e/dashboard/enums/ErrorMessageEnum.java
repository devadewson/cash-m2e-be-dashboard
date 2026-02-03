package com.m2e.dashboard.enums;

import lombok.Getter;

@Getter
public enum ErrorMessageEnum {

    MSG_ERROR("General error. Please contact IT"),
    MSG_UNEXPECTED_ERROR("Unexpected error"),
    MSG_TASK_NOT_EXIST("Task does not exist."),
    MSG_TASK_CANT_APPROVE("Task cannot be approved"),
    MSG_TASK_CANT_REJECT("Task cannot be rejected"),
    MSG_TASK_CANT_RETURN("Task cannot be return"),
    MSG_TASK_CANT_LOCK("Task cannot be lock"),
    MSG_TASK_CANT_UNLOCK("Task cannot be unlock"),
    MSG_TASK_ALREADY_SUBMITTED("Task already submitted"),
    MSG_USER_GROUP_ALREADY_EXIST("User group name existed"),
    MSG_USER_GROUP_NOT_EXIST("User group name does not exist"),
    MSG_USER_ID_NOT_EXIST("User ID does not exist"),
    MSG_USER_ID_EXIST("User ID already existed"),
    MSG_USER_ID_ACTIVATED("User ID already activated"),
    MSG_USER_ID_DEACTIVATED("User ID already deactivated"),
    MSG_WORK_REF_NOT_FOUND("Work ref id does not exist"),
    MSG_USER_GROUP_CONTAIN_USER("User Group contain user"),
    MSG_INVALID_REQUEST("Invalid request"),
    MSG_NOT_FOUND("Not found"),
    MSG_EXIST("Item already exist"),
    MSG_DUPLICATE("Duplicate Item"),
    MSG_NOT_ALLOWED("Not allowed"),
    MSG_WIDGET_ALREADY_EXIST("Widget already exist"),
    MSG_WIDGET_NOT_EXIST("Widget not exist"),
    MSG_ID_NOT_FOUND("The entity does not exist"),
    MSG_OPERATION_HOUR_NOT_EXIST("Operation hour not exist"),
    MSG_FILE_LIMIT_EXCEED("The file limit has been exceeded"),
    MSG_SIZE_LIMIT_EXCEED("Exceed image size limit"),
    MSG_FILE_EXT_INVALID("The file format is invalid"),
    MSG_INVALID_DIMENSION("The file does not have a dimension"),
    DOWNTIME_ERROR("Operation cannot proceed due to downtime"),
    OPERATION_HOUR_ERROR("Operation cannot proceed due out of operational hours"),
    MSG_ACCT_INQ_NOT_FOUND("No record found for the selected provider"),
    MSG_ESB_TIMEOUT("ESB Timeout");
    private final String description;

    ErrorMessageEnum(String description) {
        this.description = description;
    }

}
