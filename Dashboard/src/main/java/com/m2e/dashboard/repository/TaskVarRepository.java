package com.m2e.dashboard.repository;

import com.m2e.dashboard.projection.TaskVarProjection;
import com.m2e.model.sso.base.CfoTaskVar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskVarRepository extends JpaRepository<CfoTaskVar, String> {
    @Query(value = "SELECT t.* FROM TBL_CFO_TASK_VAR@CTS_SSO t WHERE t.CFO_TASK_ID = :taskId", nativeQuery = true)
    List<CfoTaskVar> findByTaskId(String taskId);
    @Query(value = "SELECT t.id as id FROM TBL_CFO_TASK_VAR@CTS_SSO t WHERE t.CFO_TASK_ID = :taskId", nativeQuery = true)
    List<TaskVarProjection> findIdsByTaskId(String taskId);
}
