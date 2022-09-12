package com.topably.assets.portfolios.service;

import com.topably.assets.core.domain.TickerSymbol;
import com.topably.assets.exchanges.service.ExchangeService;
import com.topably.assets.instruments.domain.Instrument;
import com.topably.assets.portfolios.domain.Portfolio;
import com.topably.assets.portfolios.domain.PortfolioHolding;
import com.topably.assets.portfolios.domain.PortfolioHoldingView;
import com.topably.assets.portfolios.domain.dto.PortfolioHoldingDto;
import com.topably.assets.portfolios.repository.PortfolioHoldingRepository;
import com.topably.assets.portfolios.repository.PortfolioRepository;
import com.topably.assets.trades.domain.dto.AggregatedTradeDto;
import com.topably.assets.trades.domain.dto.add.AddTradeDto;
import com.topably.assets.xrates.service.currency.CurrencyConverterService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
public class PortfolioHoldingServiceImpl implements PortfolioHoldingService {

    private final PortfolioHoldingRepository portfolioHoldingRepository;
    private final PortfolioRepository portfolioRepository;

    private final ExchangeService exchangeService;
    private final CurrencyConverterService currencyConverterService;

    @Override
    public Optional<PortfolioHolding> findByUserIdAndInstrumentId(Long userId, Long instrumentId) {
        return portfolioHoldingRepository.findByPortfolio_User_IdAndInstrument_Id(userId, instrumentId);
    }

    @Override
    public PortfolioHolding updatePortfolioHolding(Long holdingId, AggregatedTradeDto dto) {
        PortfolioHolding holding = portfolioHoldingRepository.getById(holdingId);
        holding.setQuantity(dto.getQuantity());
        holding.setAveragePrice(dto.getPrice());
        return portfolioHoldingRepository.save(holding);
    }

    @Override
    public PortfolioHolding createHolding(AddTradeDto dto, Instrument instrument) {
        Portfolio portfolio = portfolioRepository.findByUserId(dto.getUserId());
        return portfolioHoldingRepository.saveAndFlush(PortfolioHolding.builder()
                .portfolio(portfolio)
                .instrument(instrument)
                .quantity(dto.getQuantity())
                .averagePrice(dto.getPrice())
                .build());
    }

    @Override
    public Collection<PortfolioHoldingDto> findPortfolioHoldings(Long portfolioId) {
        return portfolioHoldingRepository.findAllByPortfolioId(portfolioId).stream()
                .map(holding -> {
                    Instrument instrument = holding.getInstrument();
                    return PortfolioHoldingDto.builder()
                            .id(holding.getId())
                            .instrumentId(instrument.getId())
                            .instrumentType(instrument.getInstrumentType())
                            .identifier(instrument.toTickerSymbol())
                            .currency(instrument.getExchange().getCurrency())
                            .quantity(holding.getQuantity())
                            .price(holding.getAveragePrice())
                            .build();
                }).toList();
    }

    @Override
    @Transactional
    public Collection<PortfolioHoldingDto> findPortfolioHoldingsByUserId(Long userId) {
        var portfolio = portfolioRepository.findByUserId(userId);
        return findPortfolioHoldings(portfolio.getId());
    }

    @Override
    @Transactional
    public Collection<PortfolioHoldingView> findPortfolioHoldingsView(Long userId) {
        var portfolio = portfolioRepository.findByUserId(userId);
        var holdings = portfolioHoldingRepository.findAllByPortfolioId(portfolio.getId());
        var tickerSymbolByFinData = collectHoldingFinancialData(holdings);
        var portfolioMarketValue = tickerSymbolByFinData.values().stream()
                .map(PortfolioHoldingFinancialData::convertedMarketValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        AtomicReference<BigDecimal> pctTotal = new AtomicReference<>(BigDecimal.ZERO);
        return IntStream.range(0, holdings.size())
                .mapToObj(i -> {
                    PortfolioHolding holding = holdings.get(i);
                    Instrument instrument = holding.getInstrument();
                    TickerSymbol tickerSymbol = instrument.toTickerSymbol();
                    var finData = tickerSymbolByFinData.get(tickerSymbol);
                    BigDecimal pctOfPortfolio;
                    if (i == holdings.size() - 1) {
                        pctOfPortfolio = BigDecimal.valueOf(100).subtract(pctTotal.get());
                    } else  {
                        pctOfPortfolio = BigDecimal.valueOf(100).multiply(finData.convertedMarketValue())
                                .divide(portfolioMarketValue, 2, RoundingMode.HALF_EVEN);
                    }
                    pctTotal.getAndAccumulate(pctOfPortfolio, BigDecimal::add);
                    return PortfolioHoldingView.builder()
                            .id(holding.getId())
                            .instrumentId(instrument.getId())
                            .instrumentType(instrument.getInstrumentType())
                            .identifier(tickerSymbol)
                            .currencySymbol(instrument.getExchange().getCurrency().getSymbol())
                            .quantity(holding.getQuantity())
                            .price(holding.getAveragePrice())
                            .pctOfPortfolio(pctOfPortfolio)
                            .marketValue(finData.marketValue())
                            .build();
                }).collect(Collectors.toList());
    }

    private Map<TickerSymbol, PortfolioHoldingFinancialData> collectHoldingFinancialData(List<PortfolioHolding> holdings) {
        return holdings.stream()
                .map(holding -> {
                    Instrument instrument = holding.getInstrument();
                    Currency currency = instrument.getExchange().getCurrency();
                    TickerSymbol tickerSymbol = instrument.toTickerSymbol();
                    var marketValue = exchangeService.findSymbolRecentPrice(tickerSymbol)
                            .map(value -> value.multiply(new BigDecimal(holding.getQuantity())))
                            .orElse(holding.getTotal());
                    var convertedMarketValue = currencyConverterService.convert(marketValue, currency);

                    return new PortfolioHoldingFinancialData(tickerSymbol, marketValue, convertedMarketValue);
                })
                .collect(Collectors.toMap(PortfolioHoldingFinancialData::tickerSymbol, Function.identity()));
    }

    private record PortfolioHoldingFinancialData(TickerSymbol tickerSymbol, BigDecimal marketValue,
                                                 BigDecimal convertedMarketValue) {

    }

    @Override
    public void deletePortfolioHolding(Long holdingId) {
        portfolioHoldingRepository.deleteById(holdingId);
    }
}
