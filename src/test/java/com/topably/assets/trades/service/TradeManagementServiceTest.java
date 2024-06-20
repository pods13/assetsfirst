package com.topably.assets.trades.service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;

import com.topably.assets.core.domain.Ticker;
import com.topably.assets.instruments.domain.InstrumentType;
import com.topably.assets.instruments.domain.dto.ImportInstrumentDto;
import com.topably.assets.instruments.repository.InstrumentRepository;
import com.topably.assets.instruments.service.importer.DefaultInstrumentImporter;
import com.topably.assets.integration.base.IT;
import com.topably.assets.integration.base.IntegrationTestBase;
import com.topably.assets.portfolios.service.PortfolioPositionService;
import com.topably.assets.trades.domain.TradeOperation;
import com.topably.assets.trades.domain.dto.manage.AddTradeDto;
import com.topably.assets.trades.domain.dto.manage.DeleteTradeDto;
import com.topably.assets.trades.service.broker.BrokerService;
import com.topably.assets.trades.service.manage.TradeManagementService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.topably.assets.findata.exchanges.domain.ExchangeEnum.NYSE;
import static org.assertj.core.api.Assertions.assertThat;


@IT
public class TradeManagementServiceTest extends IntegrationTestBase {

    @Autowired
    private DefaultInstrumentImporter importer;
    @Autowired
    private TradeManagementService tradeManagementService;
    @Autowired
    private InstrumentRepository instrumentRepository;
    @Autowired
    private PortfolioPositionService portfolioPositionService;
    @Autowired
    private BrokerService brokerService;

    @Test
    public void givenUserAddsNewTrade_whenInstrumentIsNotInPortfolio_thenNewPositionIsCreated() {
        var ticker = new Ticker("TEST", NYSE.name());
        var stockDto = importer.importInstrument(new ImportInstrumentDto()
            .setIdentifier(ticker)
            .setName("Test Company")
            .setSector("Энергетика")
            .setIndustry("Газ и нефть")
            .setType(InstrumentType.STOCK));
        var userId = 1L;
        var brokers = brokerService.getBrokers(userId);

        var tradeDto = tradeManagementService.addTrade(AddTradeDto.builder()
            .instrumentId(stockDto.getId())
            .operation(TradeOperation.BUY)
            .date(LocalDate.now())
            .price(BigDecimal.TEN)
            .quantity(BigInteger.TEN)
            .userId(userId)
            .intermediaryId(brokers.iterator().next().id())
            .build(), instrumentRepository.findBySymbolAndExchangeCode(ticker.getSymbol(), ticker.getExchange()));

        assertThat(tradeDto.getId()).isNotNull();
    }

    @Test
    public void givenUserDeleteTrade_whenItsTheLastTradeOfPosition_thenPositionIsDeleted() {
        var ticker = new Ticker("TEST", NYSE.name());
        var stockDto = importer.importInstrument(new ImportInstrumentDto()
            .setIdentifier(ticker)
            .setName("Test Company")
            .setSector("Энергетика")
            .setIndustry("Газ и нефть")
            .setType(InstrumentType.STOCK));
        ;

        var instrument = instrumentRepository.findBySymbolAndExchangeCode(ticker.getSymbol(), ticker.getExchange());
        var userId = 1L;
        var brokers = brokerService.getBrokers(userId);

        var tradeDto = tradeManagementService.addTrade(AddTradeDto.builder()
            .instrumentId(stockDto.getId())
            .operation(TradeOperation.BUY)
            .date(LocalDate.now())
            .price(BigDecimal.TEN)
            .quantity(BigInteger.TEN)
            .userId(userId)
            .intermediaryId(brokers.iterator().next().id())
            .build(), instrument);

        assertThat(portfolioPositionService.findPortfolioPositionsByUserId(userId).stream()
            .anyMatch(dto -> dto.getInstrumentId().equals(instrument.getId()))).isTrue();

        tradeManagementService.deleteTrade(new DeleteTradeDto(tradeDto.getId(), instrument.getId()), instrument);

        assertThat(portfolioPositionService.findPortfolioPositionsByUserId(userId).stream()
            .noneMatch(dto -> dto.getInstrumentId().equals(instrument.getId()))).isTrue();
    }

}
