package com.m2e.dashboard.service.overview.Refactor;


/** Satu sumber data overview gagal. Pesannya dikirim ke FE sebagai errorMessage. */
public class OverviewSourceException extends RuntimeException {

    public OverviewSourceException(String message) {
        super(message);
    }
}