package com.topably.assets.trades.service;

import com.topably.assets.companies.domain.Company;
import com.topably.assets.companies.repository.CompanyRepository;
import com.topably.assets.core.domain.Ticker;
import com.topably.assets.exchanges.domain.Exchange;
import com.topably.assets.exchanges.repository.ExchangeRepository;
import com.topably.assets.instruments.domain.Instrument;
import com.topably.assets.instruments.domain.instrument.Stock;
import com.topably.assets.instruments.repository.instrument.StockRepository;
import com.topably.assets.integration.base.IT;
import com.topably.assets.integration.base.IntegrationTestBase;
import com.topably.assets.trades.domain.TradeOperation;
import com.topably.assets.trades.domain.broker.Broker;
import com.topably.assets.trades.domain.dto.add.AddTradeDto;
import com.topably.assets.trades.repository.broker.BrokerRepository;
import com.topably.assets.trades.service.broker.BrokerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.Currency;

import static org.junit.jupiter.api.Assertions.*;

@IT
public class TradeManagementServiceTest extends IntegrationTestBase {

    @Autowired
    private TradeManagementService tradeManagementService;
}
