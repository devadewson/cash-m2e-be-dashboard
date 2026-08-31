package com.m2e.dashboard.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class CorpCIFRepository {

    private final EntityManager entityManager;

    /** Ambil CIF_NO dari TBL_CORP berdasarkan CORP_ID di JWT. Null kalau tidak ketemu. */
    public String findCifNoByCorpId(String corpId) {
        if (corpId == null || corpId.isBlank()) {
            return null;
        }

        Query query = entityManager.createNativeQuery(
                "SELECT CIF_NO FROM SSOPORTAL.TBL_CORP " +
                        "WHERE CORP_ID = :corpId AND CIF_NO IS NOT NULL AND ROWNUM = 1");
        query.setParameter("corpId", corpId.trim());

        try {
            Object result = query.getSingleResult();
            return result != null ? result.toString().trim() : null;
        } catch (NoResultException e) {
            return null;
        }
    }

    /** Seluruh CIF aktif, untuk job snapshot. */    @SuppressWarnings("unchecked")
    public List<String> findAllCifNo() {
        return entityManager.createNativeQuery(
                        "SELECT DISTINCT CIF_NO FROM SSOPORTAL.TBL_CORP " +
                                "WHERE CIF_NO IS NOT NULL ORDER BY CIF_NO")
                .getResultList();
    }
}