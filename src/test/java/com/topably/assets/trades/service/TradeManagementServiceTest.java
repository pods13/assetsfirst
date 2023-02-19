package com.topably.assets.trades.service;

import com.topably.assets.integration.base.IT;
import com.topably.assets.integration.base.IntegrationTestBase;
import org.springframework.beans.factory.annotation.Autowired;

@IT
public class TradeManagementServiceTest extends IntegrationTestBase {

    @Autowired
    private TradeManagementService tradeManagementService;
}
