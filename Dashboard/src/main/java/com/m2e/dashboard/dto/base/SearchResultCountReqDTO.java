package com.m2e.dashboard.dto.base;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "product",
        defaultImpl = SearchResultCountReqDTO.class,
        visible = true
)
@JsonSubTypes({
    @JsonSubTypes.Type(value=SearchResultCountAFReqDTO.class, name="AUTOFINANCE")
})
public class SearchResultCountReqDTO {
    private String product;
    private String subProduct;
    private String searchParam;
    private String corpCifNo;
}
