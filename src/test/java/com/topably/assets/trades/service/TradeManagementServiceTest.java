package com.topably.assets.trades.service;

import com.topably.assets.companies.domain.Company;
import com.topably.assets.companies.domain.dto.CompanyDataDto;
import com.topably.assets.core.domain.Ticker;
import com.topably.assets.findata.exchanges.domain.Exchange;
import com.topably.assets.findata.exchanges.domain.USExchange;
import com.topably.assets.instruments.domain.Instrument;
import com.topably.assets.instruments.domain.InstrumentType;
import com.topably.assets.instruments.domain.dto.StockDataDto;
import com.topably.assets.instruments.domain.dto.StockDto;
import com.topably.assets.instruments.domain.instrument.Stock;
import com.topably.assets.instruments.repository.InstrumentRepository;
import com.topably.assets.instruments.service.InstrumentService;
import com.topably.assets.instruments.service.StockService;
import com.topably.assets.integration.base.IT;
import com.topably.assets.integration.base.IntegrationTestBase;
import com.topably.assets.trades.domain.TradeOperation;
import com.topably.assets.trades.domain.dto.TradeDto;
import com.topably.assets.trades.domain.dto.add.AddTradeDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.Year;
import java.util.Currency;

import static org.assertj.core.api.Assertions.assertThat;

@IT
public class TradeManagementServiceTest extends IntegrationTestBase {

    @Autowired
    private StockService stockService;
    @Autowired
    private TradeManagementService tradeManagementService;
    @Autowired
    private InstrumentRepository instrumentRepository;

    @Test
    public void givenUserAddsNewTrade_whenInstrumentIsNotInPortfolio_thenNewPositionIsCreated() {
        var ticker = new Ticker("TEST", USExchange.NYSE.name());
        var stockDto = stockService.importStock(StockDataDto.builder()
            .identifier(ticker)
            .company(
                CompanyDataDto.builder().name("Test Company")
                    .industry("TEST Industry")
                    .sector("TEST Sector")
                    .build())
            .build());

        var tradeDto = tradeManagementService.addTrade(AddTradeDto.builder()
            .instrumentId(stockDto.getId())
            .operation(TradeOperation.BUY)
            .date(LocalDate.now())
            .price(BigDecimal.TEN)
            .quantity(BigInteger.TEN)
            .userId(1L)
            .brokerId(1L)
            .build(), instrumentRepository.findByTickerAndExchange_Code(ticker.getSymbol(), ticker.getExchange()));

        assertThat(tradeDto.getId()).isNotNull();
    }
}
