package com.m2e.dashboard.repository;

import com.m2e.model.sso.base.Preference;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface PreferenceRepository extends JpaRepository<Preference, String> {

    @Query(value = "SELECT * FROM TBL_PREF@CTS_SSO WHERE USR_ID = :usrId AND SUB_PRODUCT = :product AND SCREEN = :screen", nativeQuery = true)
    Optional<Preference> findByUsrIdAndSubProductAndScreen(String usrId, String product, String screen);

    @Modifying
    @Query(value = "INSERT INTO TBL_PREF@CTS_SSO (ID, SUB_PRODUCT, SCREEN, USR_ID, ATTRIBUTE) VALUES (:id, :subProduct, :screen, :usrId, :attribute)", nativeQuery = true)
    void insertPreferenceRecord(String id, String subProduct, String screen, String usrId, String attribute);

    @Modifying
    @Query(value = "DELETE FROM TBL_PREF@CTS_SSO WHERE ID = :id", nativeQuery = true)
    void deletePreferenceRecord(String id);
}
