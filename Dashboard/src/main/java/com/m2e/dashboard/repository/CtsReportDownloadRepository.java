package com.m2e.dashboard.repository;

import com.m2e.cts.CtsReportDownload;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface CtsReportDownloadRepository extends JpaRepository<CtsReportDownload, String>, JpaSpecificationExecutor<CtsReportDownload> {
}
