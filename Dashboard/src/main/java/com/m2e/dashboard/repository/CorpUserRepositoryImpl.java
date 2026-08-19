package com.m2e.dashboard.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Slf4j
public class CorpUserRepositoryImpl implements CorpUserCustomRepository {

    @PersistenceContext
    private EntityManager entityManager;

    private static final Set<String> ALLOWED_COLUMNS = Set.of(
            "IS_BD_OV_EXPLORED", "IS_BD_DP_EXPLORED", "IS_BD_LN_EXPLORED",
            "IS_BD_STMT_EXPLORED", "IS_BD_AF_EXPLORED", "IS_BD_IV_EXPLORED",
            "IS_BD_CCD_EXPLORED", "IS_BD_TD_EXPLORED", "IS_BD_OB_EXPLORED"
    );

    @Override
    @Transactional
    public int updateDemoScreenExplored(String userId,String corpID, String columnName) {
        if (!ALLOWED_COLUMNS.contains(columnName)) {
            log.error("[DemoScreen] Rejected update, column not whitelisted: {}", columnName);
            return 0;
        }

        String sql = "UPDATE TBL_CORP_USR@CTS_SSO SET " + columnName + " = 'Y' WHERE USR_ID = :userId AND CORP_ID = :corpID";

        int updated = entityManager.createNativeQuery(sql)
                .setParameter("userId", userId)
                .setParameter("corpID", corpID)
                .executeUpdate();

        log.info("[DemoScreen] Updated column={}, userId={},corpId={}, rowsAffected={}", columnName, userId,corpID, updated);
        return updated;
    }
}