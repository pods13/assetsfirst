package com.topably.assets.trades.listener;

import com.topably.assets.core.config.cache.CacheNames;
import com.topably.assets.trades.domain.event.TradeChangedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.Objects;

@Component
@Slf4j
public class TradeChangedListener {

    private final CacheManager longLivedCacheManager;

    public TradeChangedListener(@Qualifier("longLivedCacheManager") CacheManager longLivedCacheManager) {
        this.longLivedCacheManager = longLivedCacheManager;
    }

    @TransactionalEventListener
    public void onApplicationEvent(TradeChangedEvent event) {
        Objects.requireNonNull(longLivedCacheManager.getCache(CacheNames.PORTFOLIOS_LL)).invalidate();
        Objects.requireNonNull(longLivedCacheManager.getCache(CacheNames.DIVIDENDS_LL)).invalidate();
        log.info("Caches were invalidated");
    }
}
