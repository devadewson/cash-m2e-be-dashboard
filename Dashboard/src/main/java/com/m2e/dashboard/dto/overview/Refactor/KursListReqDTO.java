package com.m2e.dashboard.dto.overview.Refactor;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class KursListReqDTO {

    @JsonProperty("tenor")
    private String tenor;
    @JsonProperty("clientName")
    private String clientName;
}