package com.m2e.dashboard.repository;

import com.m2e.model.sso.base.CorpUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CorpUserRepository extends JpaRepository<CorpUser, String>, CorpUserCustomRepository {

    @Query(value = "SELECT usr_nm FROM TBL_CORP_USR@CTS_SSO WHERE USR_ID = :userId", nativeQuery = true)
    Optional<String> findByUserId(@Param("userId") String userId);
}