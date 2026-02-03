package com.m2e.dashboard.dto;

import java.sql.Timestamp;


public interface UserDetailSearch {
    String getUserId();
    String getUserName();
    Timestamp getUpdatedDate();
    String getIsDeactivated();

}
