package com.m2e.dashboard.repository;

import com.m2e.dashboard.projection.TaskProjection;
import com.m2e.model.sso.base.CfoTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<CfoTask, String> {
    @Query(value = "SELECT id as id, CFO_TASK_ID as taskId, REF_NO as refNumber, MT_APPR_STS as apprSts, CFO_FUNCS_ID as function, CFO_FUNCS_ACTION_ID as functionAction, CREATED_BY as createdBy, TO_CHAR(DT_CREATED, 'DD MON YYYY | HH24:MI:SS') as createdDate \n" +
            "FROM TBL_CFO_TASK@CTS_SSO t \n" +
            "WHERE t.CFO_TASK_ID = :taskId \n",
            nativeQuery = true)
    Optional<TaskProjection> findByTaskId(String taskId);
}
