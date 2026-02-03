package com.m2e.dashboard.sp;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class BaseSPExecDTO implements Serializable {
    @JsonProperty("STATUSCODE")
    private String statusCode;
    @JsonProperty("STATUSMESSAGE")
    private String statusMessage;
}
