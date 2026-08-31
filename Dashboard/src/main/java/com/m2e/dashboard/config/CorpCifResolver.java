package com.m2e.dashboard.config;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CorpCifResolver {

    private final CorpCIFRepository corpRepository;

    /**
     * Terjemahkan CORP_ID dari JWT menjadi CIF_NO.
     * Throw kalau tidak ketemu — jangan lanjut dengan CIF null.
     */
    @Cacheable(value = "corp-cif", key = "#corpId", unless = "#result == null")
    public String resolveCif(String corpId) {
        String cif = corpRepository.findCifNoByCorpId(corpId);

        if (cif == null) {
            log.error("CIF tidak ditemukan di TBL_CORP untuk corpId={}", corpId);
            throw new IllegalStateException("CIF tidak ditemukan untuk corporate ini");
        }

        log.debug("Resolved corpId={} -> cif={}", corpId, cif);
        return cif;
    }
}
