package com.topably.assets.portfolios.job;

import com.topably.assets.portfolios.service.PortfolioPositionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

@Component
@ConditionalOnProperty(prefix = "app.jobs.realized-pnl", name = "enabled", havingValue = "true")
@RequiredArgsConstructor
@Slf4j
public class RealizedPnlCalculationJob {

    private final PortfolioPositionService portfolioPositionService;

    @PostConstruct
    public void postInit() {
        log.info("Realized pnl calculation job is enabled");
    }

    @Scheduled(cron = "${app.jobs.realized-pnl.cron}")
    public void calculateClosedPnl() {
        portfolioPositionService.updatePnlOnClosedPositions();
    }
}
