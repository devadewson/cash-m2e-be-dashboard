package com.m2e.dashboard.dto;

import com.m2e.dashboard.enums.AdminEventEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class NotificationProcessingRequest {
    private String makerUserId;
    private String ownerId;
    private AdminEventEnum eventAction;
    private Map<String, String> data;
}
