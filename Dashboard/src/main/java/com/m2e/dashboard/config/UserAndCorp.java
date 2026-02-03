package com.m2e.dashboard.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserAndCorp {
    private String userId;
    private String corpId;
    private String mainCorpId;
}
