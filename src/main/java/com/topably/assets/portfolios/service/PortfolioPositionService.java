package com.topably.assets.portfolios.service;

import com.topably.assets.core.domain.Ticker;
import com.topably.assets.dividends.service.DividendService;
import com.topably.assets.exchanges.service.ExchangeService;
import com.topably.assets.instruments.domain.Instrument;
import com.topably.assets.portfolios.domain.Portfolio;
import com.topably.assets.portfolios.domain.PortfolioPosition;
import com.topably.assets.portfolios.domain.PortfolioPositionView;
import com.topably.assets.portfolios.domain.dto.PortfolioPositionDto;
import com.topably.assets.portfolios.repository.PortfolioPositionRepository;
import com.topably.assets.portfolios.repository.PortfolioRepository;
import com.topably.assets.trades.domain.dto.AggregatedTradeDto;
import com.topably.assets.trades.domain.dto.add.AddTradeDto;
import com.topably.assets.trades.service.TradeAggregatorService;
import com.topably.assets.xrates.service.currency.CurrencyConverterService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Year;
import java.time.ZoneOffset;
import java.util.Collection;
import java.util.Currency;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
@Slf4j
public class PortfolioPositionService {

    private final PortfolioPositionRepository portfolioPositionRepository;
    private final PortfolioRepository portfolioRepository;

    private final ExchangeService exchangeService;
    private final CurrencyConverterService currencyConverterService;
    private final DividendService dividendService;
    private final TradeAggregatorService tradeAggregatorService;

    public Optional<PortfolioPosition> findByUserIdAndInstrumentId(Long userId, Long instrumentId) {
        return portfolioPositionRepository.findByPortfolio_User_IdAndInstrument_Id(userId, instrumentId);
    }

    public PortfolioPosition updatePortfolioPosition(Long positionId, AggregatedTradeDto dto) {
        var position = portfolioPositionRepository.getById(positionId);
        position.setQuantity(dto.getQuantity());
        position.setAveragePrice(dto.getPrice());
        return portfolioPositionRepository.save(position);
    }

    public PortfolioPosition createPosition(AddTradeDto dto, Instrument instrument) {
        Portfolio portfolio = portfolioRepository.findByUserId(dto.getUserId());
        return portfolioPositionRepository.saveAndFlush(PortfolioPosition.builder()
            .portfolio(portfolio)
            .instrument(instrument)
            .quantity(dto.getQuantity())
            .averagePrice(dto.getPrice())
            .build());
    }

    public Collection<PortfolioPositionDto> findPortfolioPositions(Long portfolioId) {
        return portfolioPositionRepository.findAllByPortfolioId(portfolioId).stream()
            .map(position -> {
                Instrument instrument = position.getInstrument();
                return PortfolioPositionDto.builder()
                    .id(position.getId())
                    .instrumentId(instrument.getId())
                    .instrumentType(instrument.getInstrumentType())
                    .identifier(instrument.toTicker())
                    .currency(instrument.getCurrency())
                    .quantity(position.getQuantity())
                    .price(position.getAveragePrice())
                    .build();
            }).toList();
    }

    public Collection<Long> findAllPositionIdsByPortfolioId(Long portfolioId) {
        return portfolioPositionRepository.findAllPositionIdsByPortfolioId(portfolioId);
    }

    @Transactional
    public Collection<PortfolioPositionDto> findPortfolioPositionsByUserId(Long userId) {
        var portfolio = portfolioRepository.findByUserId(userId);
        return findPortfolioPositions(portfolio.getId());
    }

    @Transactional
    public Collection<PortfolioPositionView> findPortfolioPositionsView(Long userId) {
        var portfolio = portfolioRepository.findByUserId(userId);
        var positions = portfolioPositionRepository.findAllByPortfolioId(portfolio.getId());
        var tickerByFinData = collectPositionFinancialData(positions);
        var portfolioMarketValue = tickerByFinData.values().stream()
            .map(PortfolioPositionFinancialData::convertedMarketValue)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        AtomicReference<BigDecimal> pctTotal = new AtomicReference<>(BigDecimal.ZERO);
        return IntStream.range(0, positions.size())
            .mapToObj(i -> {
                var position = positions.get(i);
                Instrument instrument = position.getInstrument();
                Ticker ticker = instrument.toTicker();
                var finData = tickerByFinData.get(ticker);
                BigDecimal pctOfPortfolio;
                if (i == positions.size() - 1) {
                    pctOfPortfolio = BigDecimal.valueOf(100).subtract(pctTotal.get());
                } else {
                    pctOfPortfolio = BigDecimal.valueOf(100).multiply(finData.convertedMarketValue())
                        .divide(portfolioMarketValue, 2, RoundingMode.HALF_EVEN);
                }
                pctTotal.getAndAccumulate(pctOfPortfolio, BigDecimal::add);
                return PortfolioPositionView.builder()
                    .id(position.getId())
                    .instrumentId(instrument.getId())
                    .instrumentType(instrument.getInstrumentType())
                    .identifier(ticker)
                    .currencySymbol(instrument.getCurrency().getSymbol())
                    .quantity(position.getQuantity())
                    .price(position.getAveragePrice())
                    .pctOfPortfolio(pctOfPortfolio)
                    .marketValue(finData.marketValue())
                    .yieldOnCost(finData.yieldOnCost)
                    .build();
            }).collect(Collectors.toList());
    }

    private Map<Ticker, PortfolioPositionFinancialData> collectPositionFinancialData(List<PortfolioPosition> positions) {
        return positions.stream()
            .map(position -> {
                var instrument = position.getInstrument();
                var currency = instrument.getCurrency();
                Ticker ticker = instrument.toTicker();
                var marketValue = exchangeService.findSymbolRecentPrice(ticker)
                    .map(value -> value.multiply(new BigDecimal(position.getQuantity())))
                    .orElse(position.getTotal());
                var convertedMarketValue = currencyConverterService.convert(marketValue, currency);

                return new PortfolioPositionFinancialData(ticker, marketValue, convertedMarketValue,
                    calculateYieldOnCost(position));
            })
            .collect(Collectors.toMap(PortfolioPositionFinancialData::ticker, Function.identity()));
    }

    private BigDecimal calculateYieldOnCost(PortfolioPosition position) {
        var annualDividend = dividendService.calculateAnnualDividend(position.getInstrument().toTicker(), Year.now());
        return Optional.ofNullable(position.getAveragePrice())
            .filter(price -> price.compareTo(BigDecimal.ZERO) > 0)
            .map(price -> BigDecimal.valueOf(100).multiply(annualDividend).divide(price, RoundingMode.HALF_EVEN))
            .orElse(BigDecimal.ZERO);
    }

    private record PortfolioPositionFinancialData(Ticker ticker, BigDecimal marketValue,
                                                  BigDecimal convertedMarketValue,
                                                  BigDecimal yieldOnCost) {

    }

    public void deletePortfolioPosition(Long positionId) {
        portfolioPositionRepository.deleteById(positionId);
    }

    public BigDecimal calculateInvestedAmountByPositionId(Long positionId, Currency portfolioCurrency) {
        var tradesResult = tradeAggregatorService.aggregateTradesByPositionId(positionId);
        return tradesResult.getBuyTradesData().stream()
            .map(t -> {
                var total = t.getPrice().multiply(new BigDecimal(t.getShares()));
                return currencyConverterService.convert(total, t.getCurrency(), portfolioCurrency,
                    t.getTradeTime().atStartOfDay().toInstant(ZoneOffset.UTC));
            })
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
