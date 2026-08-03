package com.m2e.dashboard.model.Trapi.Response;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class KursAllResponse {

    @JsonProperty("dateCreated")
    private String dateCreated;

    @JsonProperty("respCode")
    private String respCode;

    @JsonProperty("respMsg")
    private String respMsg;

    @JsonProperty("respData")
    private List<KursRateDTO> respData;

}
