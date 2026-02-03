package com.m2e.dashboard.repository;

import com.m2e.dashboard.projection.ApprovalStatusProjection;
import com.m2e.model.sso.base.ApprovalStatusMaintenance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApprovalStatusRepository extends JpaRepository<ApprovalStatusMaintenance, String> {
    @Query(value = "select ID AS id,CD AS code,DSCP AS description from TBL_MT_APPR_STS@CTS_SSO where CD IN ('APPROVED','REJECTED','PENDING_APPR') and IS_DEACTIVATED = 'N' and IS_DEL='N'",
            nativeQuery = true)
    List<ApprovalStatusProjection> findCFOApprovalStatus();
    @Query(value = "select ID AS id,CD AS code,DSCP AS description from TBL_MT_APPR_STS@CTS_SSO where ID= :id", nativeQuery = true)
    Optional<ApprovalStatusProjection> findCFOApprovalStatusById(String id);

    @Query(value = "select ID AS id,CD AS code,DSCP AS description from TBL_MT_APPR_STS@CTS_SSO where CD= :code", nativeQuery = true)
    Optional<ApprovalStatusProjection> findByCode(String code);
}
