package com.m2e.dashboard.exception;

public class EnumNotFoundException extends RuntimeException {

    public EnumNotFoundException(String message) {
        super(message);
    }

    public EnumNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
