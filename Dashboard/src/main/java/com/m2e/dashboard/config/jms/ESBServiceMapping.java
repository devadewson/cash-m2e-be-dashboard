package com.m2e.dashboard.config.jms;

import com.m2e.dashboard.enums.ESBServiceType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ESBServiceMapping {
//    ESBServiceType value();
}

