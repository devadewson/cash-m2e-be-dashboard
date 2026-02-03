package com.m2e.dashboard.repository;

import com.m2e.dashboard.projection.CorpDetailProjection;
import com.m2e.model.sso.base.Corp;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CorpRepository extends JpaRepository<Corp, String> {
    @Query(value = "SELECT c.corp_short_nm FROM TBL_CORP@CTS_SSO c WHERE c.corp_id = :corpId", nativeQuery = true)
    Optional<String> findFirstCorpShortNameByCorpId(String corpId);

    @Query(value = "SELECT a.mt_business_seg FROM tbl_corp@cts_sso a " +
            "where a.is_del='N' AND a.corp_id = :corpId " +
            "and rownum = 1", nativeQuery = true)
    Optional<String> getMtBusinessSegCodeByCorpId(@NonNull String corpId);

    @Query(value = "SELECT pvd.cd FROM TBL_CORP@CTS_SSO c INNER JOIN TBL_MT_PVD@CTS_SSO pvd ON c.mt_pvd_id = pvd.id WHERE c.corp_id = :corpId", nativeQuery = true)
    Optional<String> findProviderByCorpId(String corpId);

    @Query(value = "SELECT c.corp_short_nm FROM TBL_CORP@CTS_SSO c WHERE c.CIF_NO = :cifNo", nativeQuery = true)
    Optional<String> findCorpShortNameByCifNo(String cifNo);

    @Query(value = "SELECT c.corp_short_nm FROM TBL_CORP@CTS_SSO c WHERE c.CIF_NO = :cifNo and c.CORP_ID = :corpId", nativeQuery = true)
    Optional<String> findCorpShortNameByCifNoAndCorpId(String cifNo, String corpId);

    @Query(value = "SELECT c.corp_short_nm as corpShortName, pvd.cd as corpProvider, c.brn as brn, c.cif_no as cifNumber FROM TBL_CORP@CTS_SSO c INNER JOIN TBL_MT_PVD@CTS_SSO pvd ON c.mt_pvd_id = pvd.id WHERE c.corp_id = :corpId", nativeQuery = true)
    Optional<CorpDetailProjection> findCorpDetailByCorpId(String corpId);

    @Query(value="SELECT d.cd " +
            "             FROM TBL_CORP@CTS_SSO c " +
            "               inner join TBL_MT_COUNTRY@CTS_SSO e on e.cd = c.sys_pvd_cd " +
            "               inner join TBL_MT_CURRENCY@CTS_SSO d on d.MT_COUNTRY_ID = e.id " +
            "              WHERE c.corp_id = :corpId",nativeQuery = true)
    Optional<String> findCurrencyByCorpProviderId(String corpId);
}
