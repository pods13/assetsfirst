package com.topably.assets.core.config.cache;

import java.util.concurrent.TimeUnit;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.cache.support.NoOpCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import static com.topably.assets.core.config.cache.CacheNames.DIVIDENDS_LL;
import static com.topably.assets.core.config.cache.CacheNames.EXCHANGES;
import static com.topably.assets.core.config.cache.CacheNames.EXCHANGE_RATES_LL;
import static com.topably.assets.core.config.cache.CacheNames.PORTFOLIOS_LL;


@Configuration
@EnableCaching
public class CacheConfig {

    @ConditionalOnProperty(value = "spring.cache.type", havingValue = "caffeine", matchIfMissing = true)
    public static class CaffeineCacheManagersConfig {

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
        @ConditionalOnProperty(value = "spring.cache.type", havingValue = "caffeine", matchIfMissing = true)
        public CacheManager longLivedCacheManager() {
            Caffeine<Object, Object> caffeineCacheBuilder = Caffeine.newBuilder()
                .maximumSize(500)
                .expireAfterWrite(1, TimeUnit.HOURS);
            CaffeineCacheManager cacheManager = new CaffeineCacheManager(EXCHANGE_RATES_LL, PORTFOLIOS_LL, DIVIDENDS_LL);
            cacheManager.setCaffeine(caffeineCacheBuilder);
            return cacheManager;
        }

    }

    @ConditionalOnProperty(value = "spring.cache.type", havingValue = "none")
    public static class NoopCacheManagersConfig {

        @Bean(name = "cacheManager")
        @Primary
        public CacheManager noopCacheManager() {
            return new NoOpCacheManager();
        }


        @Bean("longLivedCacheManager")
        public CacheManager noopLongLivedCacheManager() {
            return new NoOpCacheManager();
        }

    }

}
