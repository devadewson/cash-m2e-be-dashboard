package com.m2e.dashboard.repository;

import com.m2e.dashboard.projection.CorpDetailProjection;
import com.m2e.model.sso.base.Cif;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CifRepository extends JpaRepository<Cif, String> {
    @Query(value = "SELECT c.corp_short_nm FROM TBL_CIF@CTS_SSO c WHERE c.cif_no = :cifNo", nativeQuery = true)
    Optional<String> findFirstCorpShortNameByCifNo(String cifNo);
    @Query(value = "SELECT c.corp_short_nm as corpShortName, c.corp_long_nm as corpLongName, pvd.cd as corpProvider FROM TBL_CIF@CTS_SSO c INNER JOIN TBL_MT_PVD@CTS_SSO pvd ON c.mt_pvd_id = pvd.id WHERE c.cif_no = :cifNo", nativeQuery = true)
    Optional<CorpDetailProjection> findCorpDetailByCifNo(String cifNo);

    @Query(value = "SELECT c.corp_short_nm as corpShortName, pvd.cd as corpProvider FROM TBL_CIF@CTS_SSO c INNER JOIN TBL_MT_PVD@CTS_SSO pvd ON c.mt_pvd_id = pvd.id WHERE c.cif_no = :cifNo and c.corp_id = :corpId", nativeQuery = true)
    Optional<CorpDetailProjection> findCorpDetailByCifNoAndCorpId(String cifNo, String corpId);

    @Query(value = "SELECT pvd.cd FROM TBL_CIF@CTS_SSO c INNER JOIN TBL_MT_PVD@CTS_SSO pvd ON c.mt_pvd_id = pvd.id WHERE c.corp_id = :corpId", nativeQuery = true)
    Optional<String> findCorpProviderByCorpId(String corpId);

    @Query(value = "SELECT c.cifno FROM TBL_CIF@CTS_SSO c WHERE c.corp_id = :corpId", nativeQuery = true)
    Optional<String> findCifNoByCorpId(String corpId);
}
