package com.m2e.dashboard.repository;

import com.m2e.dashboard.projection.TaskHistoryProjection;
import com.m2e.model.sso.base.CfoTaskHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskHistoryRepository extends JpaRepository<CfoTaskHistory, String> {
    @Query(value = "SELECT t.id as id, t.MT_APPR_STS as approvalStatus, t.REMARK as remark, t.CREATED_BY as createdBy, TO_CHAR(t.DT_CREATED, 'DD MON YYYY | HH24:MI:SS') as createdDate FROM TBL_CFO_TASK_HIS@CTS_SSO t WHERE t.CFO_TASK_ID = :taskId", nativeQuery = true)
    List<TaskHistoryProjection> findByTaskId(String taskId);

    @Query(value = "SELECT t.id as id FROM TBL_CFO_TASK_HIS@CTS_SSO t WHERE t.CFO_TASK_ID = :taskId", nativeQuery = true)
    List<TaskHistoryProjection> findIdsByTaskId(String taskId);

    @Query(value = "DELETE FROM TBL_CFO_TASK_HIS@CTS_SSO WHERE id = :id", nativeQuery = true)
    void deleteByIdNative(String id);
}
