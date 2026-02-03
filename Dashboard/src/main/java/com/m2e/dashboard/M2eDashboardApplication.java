package com.m2e.dashboard;

import com.m2e.utils.jdbc.config.EnableJdbcUtil;
import com.m2e.utils.observability.config.EnableElasticApmFilter;
import com.m2e.utils.redis.config.EnableRedisUtil;
import com.m2e.utils.validator.config.EnableValidator;
import com.m2e.utils.webclient.config.EnableWebClientUtil;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@ComponentScan(
        basePackages = {
            "com.m2e.utils.jdbc",
            "com.m2e.utils.redis",
            "com.m2e.utils.webclient",
            "com.m2e.dashboard",
            "com.m2e.utils.aspect"
        })
@EntityScan(basePackages = {"com.m2e.model", "com.m2e.cts"})
@EnableJdbcUtil
@EnableRedisUtil
@EnableJpaRepositories(basePackages = {"com.m2e.dashboard.repository"})
@EnableAsync
@EnableDiscoveryClient
@EnableValidator
@EnableWebClientUtil
@EnableElasticApmFilter
@OpenAPIDefinition(
        info =
                @Info(
                        title = "Convergence - CFO",
                        version = "0.1",
                        description = "Backend API for Dashboard. Environment: ${swag.env}"),
        servers = {@Server(url = "${swag.gateway-url}", description = "Gateway")})
public class M2eDashboardApplication {
    public static void main(String[] args) {
        SpringApplication.run(M2eDashboardApplication.class, args);
    }
}
