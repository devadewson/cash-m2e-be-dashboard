package com.m2e.dashboard.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

@Data
@Component
@ConfigurationProperties(prefix = "m2e.integration")
public class M2EIntegrationEIMProperties {
    private EIMConfig eim;

    @Data
    public static class EIMConfig {
        private String url;
        private Map<String, Map<String, String>> uri;
        private String apiKey;
    }

    public String getUrl() {
        if (eim != null) {
            return eim.getUrl();
        }
        return null;
    }

    public String getUri(String entity, String action) {
        if (eim != null && eim.getUri() != null) {
            Map<String, String> entityUris = eim.getUri().get(entity);
            if (entityUris != null) {
                return entityUris.get(action);
            }
        }
        return null;
    }

    public String getApiKey() {
        if(eim != null) {
            return eim.getApiKey();
        }
        return null;
    }
}
