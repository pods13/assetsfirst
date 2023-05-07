package com.topably.assets.core.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.concurrent.TimeUnit;

@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    @Primary
    public CacheManager cacheManager() {
        Caffeine<Object, Object> caffeineCacheBuilder = Caffeine.newBuilder()
            .maximumSize(500)
            .expireAfterWrite(15, TimeUnit.MINUTES);
        CaffeineCacheManager cacheManager = new CaffeineCacheManager("exchanges");
        cacheManager.setCaffeine(caffeineCacheBuilder);
        return cacheManager;
    }

    @Bean
    public CacheManager longLivedCacheManager() {
        Caffeine<Object, Object> caffeineCacheBuilder = Caffeine.newBuilder()
            .maximumSize(500)
            .expireAfterWrite(1, TimeUnit.HOURS);
        CaffeineCacheManager cacheManager = new CaffeineCacheManager("exchange-rates", "portfolios", "dividends");
        cacheManager.setCaffeine(caffeineCacheBuilder);
        return cacheManager;
    }

}
