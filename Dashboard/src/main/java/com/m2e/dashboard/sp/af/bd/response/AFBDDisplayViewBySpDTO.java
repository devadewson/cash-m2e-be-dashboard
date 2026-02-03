package com.m2e.dashboard.sp.af.bd.response;

import lombok.Data;

import java.io.Serializable;

@Data
public class AFBDDisplayViewBySpDTO implements Serializable { // same as AFFSAcctListSpParam, without extending BasePageRequest
    private String displayViewBy;
}
