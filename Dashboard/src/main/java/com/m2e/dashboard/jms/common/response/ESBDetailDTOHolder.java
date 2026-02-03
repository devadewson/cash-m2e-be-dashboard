package com.m2e.dashboard.jms.common.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ESBDetailDTOHolder implements ESBDetailDTOAware {
    private List<ESBDetailDTO> details;
}
