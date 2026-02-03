package com.m2e.dashboard.repository;

import com.m2e.cts.UserEntitlementEntity;
import com.m2e.dashboard.dto.overview.response.MvEntitlementResDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MvEntitlementRepository extends JpaRepository<UserEntitlementEntity, String> {
    @Query(
            value = """
                SELECT corp_id, corp_usr_id, product, sub_product from mv_user_entitlement 
                where corp_id = :corpId and corp_usr_id = :userId
                """
            , nativeQuery = true
    )
    List<MvEntitlementResDTO> findByUserAndCorp(String userId, String corpId);
}
