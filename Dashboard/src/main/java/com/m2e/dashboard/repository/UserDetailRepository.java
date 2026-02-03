package com.m2e.dashboard.repository;


import com.m2e.dashboard.dto.UserDetailSearch;
import com.m2e.model.sso.base.UserDetail;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserDetailRepository extends JpaRepository<UserDetail, String> {
    Optional<UserDetail> findByUserIdAndIsDeletedFalse(String userId);

    // Native query needs count query for paginations
    @Query(value =
            """
            select DISTINCT u.usr_id as userId, u.usr_nm as userName, CAST(u.dt_updated AS TIMESTAMP) as updatedDate, u.is_deactivated as isDeactivated 
            from tbl_sec_usr u 
            left join tbl_sec_usr_pvd up on u.usr_id=up.usr_id 
            left join tbl_mt_pvd p on p.id=up.mt_pvd_id 
            left join tbl_sec_usr_usr_group uug on u.usr_id=uug.usr_id  
            left join tbl_sec_usr_group ug on uug.mt_usr_group_id=ug.id 
            where ((:userId IS NULL) OR (UPPER(u.usr_id) LIKE UPPER(('%' || :userId || '%'))))  
            and ((:userName IS NULL) OR (UPPER(u.usr_nm) LIKE UPPER(('%' || :userName || '%')))) 
            and ((:code IS NULL) OR (UPPER(ug.cd) = UPPER(:code))) 
            and ((:userProvider IS NULL) OR (UPPER(p.cd) = UPPER(:userProvider))) 
            and u.is_del = 'N' """,
            countQuery = """
            select COUNT(DISTINCT u.usr_id)
            from tbl_sec_usr u 
            left join tbl_sec_usr_pvd up on u.usr_id=up.usr_id 
            left join tbl_mt_pvd p on p.id=up.mt_pvd_id 
            left join tbl_sec_usr_usr_group uug on u.usr_id=uug.usr_id  
            left join tbl_sec_usr_group ug on uug.mt_usr_group_id=ug.id 
            where ((:userId IS NULL) OR (UPPER(u.usr_id) LIKE UPPER(('%' || :userId || '%'))))  
            and ((:userName IS NULL) OR (UPPER(u.usr_nm) LIKE UPPER(('%' || :userName || '%')))) 
            and ((:code IS NULL) OR (UPPER(ug.cd) = UPPER(:code))) 
            and ((:userProvider IS NULL) OR (UPPER(p.cd) = UPPER(:userProvider))) 
            and u.is_del = 'N' """,
            nativeQuery = true
    )
    Page<UserDetailSearch> search(
            @Param("userId") String userId,
            @Param("userName") String userName,
            @Param("code") String code,
            @Param("userProvider") String userProvider,  Pageable pageable);
}