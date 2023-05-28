package com.topably.assets.portfolios.service;

import com.topably.assets.core.domain.Ticker;
import com.topably.assets.findata.dividends.service.DividendService;
import com.topably.assets.findata.exchanges.service.ExchangeService;
import com.topably.assets.findata.xrates.service.currency.CurrencyConverterService;
import com.topably.assets.instruments.domain.Instrument;
import com.topably.assets.instruments.domain.InstrumentType;
import com.topably.assets.portfolios.domain.Portfolio;
import com.topably.assets.portfolios.domain.dto.PortfolioPositionDto;
import com.topably.assets.portfolios.domain.position.PortfolioPosition;
import com.topably.assets.portfolios.domain.position.PortfolioPositionView;
import com.topably.assets.portfolios.mapper.PortfolioPositionMapper;
import com.topably.assets.portfolios.mapper.TagMapper;
import com.topably.assets.portfolios.repository.PortfolioPositionRepository;
import com.topably.assets.portfolios.repository.PortfolioRepository;
import com.topably.assets.portfolios.repository.tag.TagRepository;
import com.topably.assets.trades.domain.dto.AggregatedTradeDto;
import com.topably.assets.trades.domain.dto.add.AddTradeDto;
import com.topably.assets.trades.service.TradeAggregatorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
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
@Transactional
public class PortfolioPositionService {

    private final PortfolioPositionRepository portfolioPositionRepository;
    private final PortfolioRepository portfolioRepository;
    private final TagRepository tagRepository;
    private final TagMapper tagMapper;
    private final PortfolioPositionMapper portfolioPositionMapper;
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
            .openDate(dto.getDate())
            .build());
    }

    public Collection<PortfolioPosition> findPortfolioPositionsByPortfolioId(Long portfolioId) {
        return portfolioPositionRepository.findAllByPortfolioId(portfolioId);
    }

    public Collection<PortfolioPositionDto> findPortfolioPositions(Long portfolioId) {
        return findPortfolioPositionsByPortfolioId(portfolioId).stream()
            .map(portfolioPositionMapper::modelToDto)
            .toList();
    }

    public Collection<PortfolioPositionDto> findPortfolioPositionsOpenedByDate(Long portfolioId, LocalDate date) {
        return portfolioPositionRepository.findAllByPortfolioIdAndOpenDateLessThanEqual(portfolioId, date).stream()
            .map(portfolioPositionMapper::modelToDto)
            .toList();
    }

    public Collection<Long> findAllPositionIdsByPortfolioId(Long portfolioId) {
        return portfolioPositionRepository.findAllPositionIdsByPortfolioId(portfolioId);
    }

    public Collection<PortfolioPositionDto> findPortfolioPositionsByUserId(Long userId) {
        var portfolio = portfolioRepository.findByUserId(userId);
        return findPortfolioPositions(portfolio.getId());
    }

    public Collection<PortfolioPositionView> findPortfolioPositionsView(Long userId, boolean hideSold) {
        var portfolio = portfolioRepository.findByUserId(userId);
        List<PortfolioPosition> positions = getPortfolioPositions(portfolio.getId(), hideSold);
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
                //TODO use portfolioPositionMapper instead
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
                    //TODO check sql query for n+1 problem
                    .tags(position.getTags().stream().map(tagMapper::modelToProjection).toList())
                    .build();
            }).collect(Collectors.toList());
    }

    private List<PortfolioPosition> getPortfolioPositions(Long portfolioId, boolean hideSold) {
        if (hideSold) {
            return portfolioPositionRepository.findAllNotSoldByPortfolioId(portfolioId);
        }
        return portfolioPositionRepository.findAllByPortfolioId(portfolioId);
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
        var annualDividend = calculateAnnualDividend(position, Year.now());
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

    public BigDecimal calculateInvestedAmountByPositionId(Long positionId, Currency portfolioCurrency, LocalDate date) {
        var tradesResult = tradeAggregatorService.aggregateTradesByPositionId(positionId, date);
        return tradesResult.getBuyTradesData().stream()
            .map(t -> {
                var total = t.getPrice().multiply(new BigDecimal(t.getShares()));
                return currencyConverterService.convert(total, t.getCurrency(), portfolioCurrency,
                    t.getTradeTime().atStartOfDay().toInstant(ZoneOffset.UTC));
            })
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public void updatePositionTags(Long positionId, Collection<Long> tagIds) {
        var position = portfolioPositionRepository.findById(positionId).orElseThrow();
        position.getTags().clear();
        var tags = tagRepository.findAllById(tagIds);
        tags.forEach(t -> position.getTags().add(t));
    }

    public BigDecimal calculateAnnualDividend(PortfolioPosition position, Year year) {
        var instrument = position.getInstrument();
        var instrumentType = instrument.getInstrumentType();
        if (!InstrumentType.STOCK.name().equals(instrumentType) && !InstrumentType.ETF.name().equals(instrumentType)) {
            return BigDecimal.ZERO;
        }
        return dividendService.calculateAnnualDividend(instrument.toTicker(), year);
    }
}
