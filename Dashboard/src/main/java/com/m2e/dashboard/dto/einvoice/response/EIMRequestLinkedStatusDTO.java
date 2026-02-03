package com.m2e.dashboard.dto.einvoice.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class EIMRequestLinkedStatusDTO implements Serializable {
    private String gcif;
    private String isLinked;
}
