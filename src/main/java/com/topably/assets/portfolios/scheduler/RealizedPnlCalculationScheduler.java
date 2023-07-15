package com.topably.assets.portfolios.scheduler;

import com.topably.assets.portfolios.service.PortfolioPositionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
@ConditionalOnProperty(prefix = "app.scheduler.realized-pnl", name = "enabled", havingValue = "true")
@RequiredArgsConstructor
@Slf4j
public class RealizedPnlCalculationScheduler {

    private final PortfolioPositionService portfolioPositionService;

    @PostConstruct
    public void postInit() {
        log.info("Realized pnl calculation scheduler is enabled");
    }

    @Scheduled(cron = "${app.scheduler.realized-pnl.cron}")
    public void calculateClosedPnl() {
        portfolioPositionService.updatePnlOnClosedPositions();
    }
}
