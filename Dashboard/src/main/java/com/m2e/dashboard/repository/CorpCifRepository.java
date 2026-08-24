package com.m2e.dashboard.repository;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class CorpCifRepository {

    private final EntityManager entityManager;

    @SuppressWarnings("unchecked")
    public List<String> findAllDistinctCif() {
        return entityManager.createNativeQuery(
                        "SELECT DISTINCT CORP_ID FROM TBL_CORP_USR " +
                                "WHERE CORP_ID IS NOT NULL ORDER BY CORP_ID")
                .getResultList();
    }
}