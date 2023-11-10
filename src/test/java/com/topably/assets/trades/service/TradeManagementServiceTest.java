package com.topably.assets.trades.service;

import com.topably.assets.companies.domain.dto.CompanyDataDto;
import com.topably.assets.core.domain.Ticker;
import com.topably.assets.findata.exchanges.domain.USExchange;
import com.topably.assets.instruments.domain.Instrument;
import com.topably.assets.instruments.domain.dto.StockDataDto;
import com.topably.assets.instruments.repository.InstrumentRepository;
import com.topably.assets.instruments.service.StockService;
import com.topably.assets.integration.base.IT;
import com.topably.assets.integration.base.IntegrationTestBase;
import com.topably.assets.portfolios.domain.dto.PortfolioPositionDto;
import com.topably.assets.portfolios.domain.position.PortfolioPosition;
import com.topably.assets.portfolios.service.PortfolioPositionService;
import com.topably.assets.trades.domain.TradeOperation;
import com.topably.assets.trades.domain.dto.DeleteTradeDto;
import com.topably.assets.trades.domain.dto.add.AddTradeDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

@IT
public class TradeManagementServiceTest extends IntegrationTestBase {

    @Autowired
    private StockService stockService;
    @Autowired
    private TradeManagementService tradeManagementService;
    @Autowired
    private InstrumentRepository instrumentRepository;
    @Autowired
    private PortfolioPositionService portfolioPositionService;

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
            .build(), instrumentRepository.findBySymbolAndExchange_Code(ticker.getSymbol(), ticker.getExchange()));

        assertThat(tradeDto.getId()).isNotNull();
    }

    @Test
    public void givenUserDeleteTrade_whenItsTheLastTradeOfPosition_thenPositionIsDeleted() {
        var ticker = new Ticker("TEST", USExchange.NYSE.name());
        var stockDto = stockService.importStock(StockDataDto.builder()
            .identifier(ticker)
            .company(
                CompanyDataDto.builder().name("Test Company")
                    .industry("TEST Industry")
                    .sector("TEST Sector")
                    .build())
            .build());

        var instrument = instrumentRepository.findBySymbolAndExchange_Code(ticker.getSymbol(), ticker.getExchange());
        var userId = 1L;
        var tradeDto = tradeManagementService.addTrade(AddTradeDto.builder()
            .instrumentId(stockDto.getId())
            .operation(TradeOperation.BUY)
            .date(LocalDate.now())
            .price(BigDecimal.TEN)
            .quantity(BigInteger.TEN)
            .userId(userId)
            .brokerId(1L)
            .build(), instrument);

        assertThat( portfolioPositionService.findPortfolioPositionsByUserId(userId).stream()
            .anyMatch(dto -> dto.getInstrumentId().equals(instrument.getId()))).isTrue();

        tradeManagementService.deleteTrade(new DeleteTradeDto(tradeDto.getId(), instrument.getId()), instrument);

        assertThat( portfolioPositionService.findPortfolioPositionsByUserId(userId).stream()
            .noneMatch(dto -> dto.getInstrumentId().equals(instrument.getId()))).isTrue();
    }
}
