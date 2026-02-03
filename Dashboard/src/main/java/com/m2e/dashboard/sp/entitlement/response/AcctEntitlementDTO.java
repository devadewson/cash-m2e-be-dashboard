package com.m2e.dashboard.sp.entitlement.response;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Data
public class AcctEntitlementDTO {
    private String acctNumber;
    private String acctProdType;
    private String acctSubProdType;
}
