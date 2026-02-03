package com.m2e.dashboard.exception;
public class BizException extends  RuntimeException{
    public BizException(String responseCode){
        super(responseCode);
    }
}
