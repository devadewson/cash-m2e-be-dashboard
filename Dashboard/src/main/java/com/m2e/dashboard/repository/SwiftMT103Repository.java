package com.m2e.dashboard.repository;

import com.m2e.cts.SwiftMT103;
import com.m2e.dashboard.projection.DepositCASwiftMT103Projection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SwiftMT103Repository extends JpaRepository<SwiftMT103, String> {

    @Query(value = "SELECT c.FILE_CONTENT as fileContent, c.FILE_TYPE as fileType FROM TBL_SWIFT_MT103 c WHERE c.REFERENCE_NO = :bankRefNo", nativeQuery = true)
    Optional<DepositCASwiftMT103Projection> findByReferenceNo(String bankRefNo);
}
