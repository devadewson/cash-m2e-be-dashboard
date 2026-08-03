package com.m2e.dashboard.repository;



import com.m2e.model.sso.base.AccountLinkBdDb;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;
@Repository
public interface AccountnoFromCorporateCIF extends JpaRepository<AccountLinkBdDb, String> {

    @Query(value = "Select acoountno from tblSentralAccount where UserID = 'userIDJWT'", nativeQuery = true)
    Optional<AccountLinkBdDb> findByBlockNo(@Param("blockNo") String blockNo);
}
