package com.m2e.dashboard.repository;

import com.m2e.dashboard.projection.PaymentTypeProjection;
import com.m2e.model.sso.base.PaymentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentTypeRepository extends JpaRepository<PaymentType, String> {
    @Query(value = "SELECT cd as code, dscp as description, provider as provider FROM TBL_PAYMENT_TYPE@CTS_SSO WHERE provider = :provider", nativeQuery = true)
    List<PaymentTypeProjection> findByProvider(String provider);
}
