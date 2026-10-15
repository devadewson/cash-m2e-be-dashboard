package com.m2e.dashboard.dto.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse implements Serializable {

    private String responseCode = "0";

    /** Hanya terisi saat gagal; tidak muncul di JSON kalau null. */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String errorMessage;

    public ErrorResponse(String responseCode) {
        this.responseCode = responseCode;
    }
}