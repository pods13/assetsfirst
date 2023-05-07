package com.topably.assets.core.config.cache;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.concurrent.TimeUnit;

import static com.topably.assets.core.config.cache.CacheNames.*;

@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    @Primary
    public CacheManager cacheManager() {
        Caffeine<Object, Object> caffeineCacheBuilder = Caffeine.newBuilder()
            .maximumSize(500)
            .expireAfterWrite(15, TimeUnit.MINUTES);
        CaffeineCacheManager cacheManager = new CaffeineCacheManager(EXCHANGES);
        cacheManager.setCaffeine(caffeineCacheBuilder);
        return cacheManager;
    }

    @Bean
    public CacheManager longLivedCacheManager() {
        Caffeine<Object, Object> caffeineCacheBuilder = Caffeine.newBuilder()
            .maximumSize(500)
            .expireAfterWrite(1, TimeUnit.HOURS);
        CaffeineCacheManager cacheManager = new CaffeineCacheManager(EXCHANGE_RATES_LL, PORTFOLIOS_LL, DIVIDENDS_LL);
        cacheManager.setCaffeine(caffeineCacheBuilder);
        return cacheManager;
    }

}
