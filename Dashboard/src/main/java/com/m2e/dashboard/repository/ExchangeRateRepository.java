package com.m2e.dashboard.repository;

import com.m2e.dashboard.projection.ConvertFXRateProjection;
import com.m2e.model.sso.base.ForeignExchange;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ExchangeRateRepository extends JpaRepository<ForeignExchange, String> {
    @Query(value = """
           SELECT mc.CD
                       FROM TBL_MT_CURRENCY@cts_sso mc
                       LEFT JOIN TBL_MT_COUNTRY@cts_sso ct ON mc.MT_COUNTRY_ID = ct.Id
                       INNER JOIN TBL_CORP@cts_sso c ON ct.CD = c.SYS_PVD_CD
                       WHERE c.CORP_ID = :corpId
                       AND mc.IS_DEL = 'N' AND ct.IS_DEL = 'N' AND c.IS_DEL = 'N'
                       AND mc.IS_DEACTIVATED = 'N' AND mc.IS_DEACTIVATED = 'N' AND c.IS_DEACTIVATED = 'N'
        """, nativeQuery = true)
    Optional<String> findBaseCurrencyByCorpId(String corpId);

    @Query(value = """
           SELECT mfe.buy_rate as buyRate, mfe.conversion_unit as conversionUnit FROM tbl_mt_foreign_exchange@cts_sso mfe
               WHERE mfe.IS_DEL = 'N' AND mfe.IS_DEACTIVATED = 'N' AND mfe.provider = :provider AND mfe.currency = :fromCurrency
        """, nativeQuery = true)
    Optional<ConvertFXRateProjection> findByProviderAndFromCurrency(String provider, String fromCurrency);

}
