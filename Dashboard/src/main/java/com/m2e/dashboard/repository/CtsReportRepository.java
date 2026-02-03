package com.m2e.dashboard.repository;

import com.m2e.cts.CtsReportEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CtsReportRepository extends JpaRepository<CtsReportEntity, String> {
}
