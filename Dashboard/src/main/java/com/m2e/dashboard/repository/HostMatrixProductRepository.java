package com.m2e.dashboard.repository;

import com.m2e.dashboard.dto.HostProductDto;
import com.m2e.model.sso.base.HostProductMatrix;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HostMatrixProductRepository extends JpaRepository<HostProductMatrix, String> {
    HostProductMatrix findByProductTypeCodeAndProviderAndIsIslamicAndIsForeignAndProductCode(String productTypeCode, String provider, String isIslamic, String isForiegn, String productCode);

    @Query(
        value="SELECT hpm.provider, hpm.is_foreign AS isForeign, hpm.is_islamic AS isIslamic, hpm.is_fitas AS isFitas, hpm.host_product_cd AS hostProductCode FROM " +
                "tbl_mt_host_prod_matrix@CTS_SSO hpm " +
                "JOIN tbl_acct@CTS_SSO a ON a.prod_cd=hpm.host_product_cd AND a.provider=hpm.provider " +
                "WHERE a.acct_no = :accountNo AND a.CURRENCY = :currency",
        nativeQuery = true
    )
    Optional<HostProductDto> findByAccountNumber(String accountNo, String currency);

    @Query(
        value = """
                    SELECT
                        a.provider,
                        hpm.is_foreign AS isForeign,
                        hpm.is_islamic AS isIslamic,
                        hpm.is_fitas AS isFitas,
                        hpm.host_product_cd AS hostProductCode
                    FROM
                        TBL_ACCT_REF@cts_sso ref
                        INNER JOIN tbl_acct@CTS_SSO a ON
                            ref.cif_no = a.CIF_NO
                            AND ref.ACCOUNT_NUMBER = :accountNo
                            AND a.CURRENCY = :currency
                        INNER JOIN tbl_mt_host_prod_matrix@CTS_SSO hpm ON hpm.PRODUCT_TYPE_CD=a.ACCT_TYP AND ref.PRODUCT_TYPE=hpm.host_product_cd AND a.provider=hpm.provider
                    WHERE
                        ref.status = 'ACTIVE'
                        AND a.ACCT_PROD_TYP = :accountProductType
                        AND a.ACCT_TYP = :accountType
                """, nativeQuery = true)
    Optional<HostProductDto> cifAsAcctNofindByAccountNumber(String accountNo, String currency, String accountProductType, String accountType);
}
