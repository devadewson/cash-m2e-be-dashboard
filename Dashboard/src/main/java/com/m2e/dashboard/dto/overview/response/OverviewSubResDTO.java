package com.m2e.dashboard.dto.overview.response;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.m2e.dashboard.enums.OvSubProductEnum;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class OverviewSubResDTO implements Comparable<OverviewSubResDTO> {

  private OvSubProductEnum subProduct;
  private String subProductDesc;
  private String currency;
  private String totNoOfAccounts;
  private String label;

  @JsonSerialize(using = ToStringSerializer.class)
  private BigDecimal totalAmount;

  private String asOf;

  @JsonSerialize(using = ToStringSerializer.class)
  private BigDecimal facilityLimit;

  @Override
  public int compareTo(final OverviewSubResDTO o) {
     if (o == null) return 0;
     if (o.subProduct == null) return 0;
     if (this.subProduct == null) return 0;
     return this.subProduct.getPosition() - o.subProduct.getPosition();
  }

}
