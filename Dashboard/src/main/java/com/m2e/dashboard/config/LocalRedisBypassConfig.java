package com.m2e.dashboard.config;

import com.m2e.utils.redis.helper.RedisHelper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("local")
public class LocalRedisBypassConfig {

    /**
     * Fake RedisHelper so Spring dependency injection passes
     */
    @Bean
    public RedisHelper redisHelper() {
        return null;
    }
}