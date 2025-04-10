package com.topably.assets.findata.splits.job;

import com.topably.assets.findata.dividends.service.DividendService;
import com.topably.assets.findata.splits.service.SplitService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(prefix = "app.jobs.dividend-split-adjustment", name = "enabled", havingValue = "true")
@RequiredArgsConstructor
@Slf4j
public class DividendSplitAdjustmentJob {

    private final SplitService splitService;
    private final DividendService dividendService;

    @PostConstruct
    public void postInit() {
        log.info("Dividend split adjustment job is enabled");
    }

    @Scheduled(cron = "${app.jobs.dividend-split-adjustment.cron}")
    public void calculateClosedPnl() {
        //TODO optimize
        //fetch the first dividend for instrument
        //check if unadjusted != adjusted then recalculate all the divs
        splitService.findInstrumentsLastSplit().forEach(split ->
                dividendService.adjustDividendAmountDueToSplit(split.getInstrument().getId()));
    }
}
