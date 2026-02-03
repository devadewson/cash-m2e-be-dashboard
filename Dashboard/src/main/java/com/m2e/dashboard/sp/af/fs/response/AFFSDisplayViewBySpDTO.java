package com.m2e.dashboard.sp.af.fs.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
public class AFFSDisplayViewBySpDTO implements Serializable { // same as AFFSAcctListSpParam, without extending BasePageRequest
    private String displayViewBy;
}
