package com.m2e.dashboard.service.esb;

public interface ESBRequestHandler<T, R> {
    R prepareRequest(T request);
    String getServiceType();
}
