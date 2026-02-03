package com.m2e.dashboard.sp.downtime.response;

import lombok.Data;

import java.io.Serializable;

@Data
public class DowntimeSpDTO implements Serializable {
    private String downtimeCategory;
    private String downtimeProvider;
    private String downtimeApp;
    private String downtimeProduct;
    private String downtimeSubProduct;
    private String provider;
}
