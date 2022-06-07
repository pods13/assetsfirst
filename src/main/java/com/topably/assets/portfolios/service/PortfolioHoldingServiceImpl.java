package com.topably.assets.portfolios.service;

import com.topably.assets.instruments.domain.Instrument;
import com.topably.assets.portfolios.domain.Portfolio;
import com.topably.assets.portfolios.domain.PortfolioHolding;
import com.topably.assets.portfolios.domain.dto.PortfolioHoldingDto;
import com.topably.assets.portfolios.repository.PortfolioHoldingRepository;
import com.topably.assets.portfolios.repository.PortfolioRepository;
import com.topably.assets.trades.domain.Trade;
import com.topably.assets.trades.domain.TradeOperation;
import com.topably.assets.trades.domain.dto.add.AddTradeDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.AbstractMap;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PortfolioHoldingServiceImpl implements PortfolioHoldingService {

    private final PortfolioHoldingRepository portfolioHoldingRepository;
    private final PortfolioRepository portfolioRepository;

    @Override
    public Optional<PortfolioHolding> findByUserIdAndInstrumentId(Long userId, Long instrumentId) {
        return portfolioHoldingRepository.findByPortfolio_User_IdAndInstrument_Id(userId, instrumentId);
    }

    @Override
    public PortfolioHolding recalculatePortfolioHolding(Long holdingId, Collection<Trade> trades) {
        var buyTradesData = new LinkedList<AbstractMap.SimpleEntry<BigInteger, BigDecimal>>();
        var closedPnl = BigDecimal.ZERO;
        for (Trade trade : trades) {
            TradeOperation operation = trade.getOperation();
            if (buyTradesData.isEmpty() && TradeOperation.SELL.equals(operation)) {
                throw new RuntimeException("Short selling is not supported");
            }
            if (buyTradesData.isEmpty() || TradeOperation.BUY.equals(operation)) {
                buyTradesData.add(new AbstractMap.SimpleEntry<>(trade.getQuantity(), trade.getPrice()));
                continue;
            }
            if (TradeOperation.SELL.equals(operation)) {
                var buySharesByPrice = buyTradesData.poll();
                closedPnl = closedPnl.add(calculatePnl(buySharesByPrice.getKey(), buySharesByPrice.getValue(),
                        trade.getQuantity(), trade.getPrice()));
                if (trade.getQuantity().compareTo(buySharesByPrice.getKey()) > 0) {
                    var remainingSharesToSell = trade.getQuantity().subtract(buySharesByPrice.getKey());
                    while (remainingSharesToSell.compareTo(BigInteger.ZERO) > 0) {
                        var nextBuySharesByPrice = buyTradesData.poll();
                        if (nextBuySharesByPrice == null) {
                            throw new RuntimeException("Short selling is not supported");
                        }
                        closedPnl = closedPnl.add(calculatePnl(nextBuySharesByPrice.getKey(), nextBuySharesByPrice.getValue(),
                                remainingSharesToSell, trade.getPrice()));
                        remainingSharesToSell = remainingSharesToSell.subtract(nextBuySharesByPrice.getKey());
                        if (remainingSharesToSell.compareTo(BigInteger.ZERO) < 0) {
                            buyTradesData.add(new AbstractMap.SimpleEntry<>(remainingSharesToSell.negate(),
                                    nextBuySharesByPrice.getValue()));
                        }
                    }
                } else if (trade.getQuantity().compareTo(buySharesByPrice.getKey()) < 0) {
                    buyTradesData.add(new AbstractMap.SimpleEntry<>(buySharesByPrice.getKey().subtract(trade.getQuantity()),
                            buySharesByPrice.getValue()));
                }
            } else {
                throw new RuntimeException(String.format("Operation %s is not supported", operation.name()));
            }
        }
        var sharesByAvgPrice = buyTradesData.stream().collect(Collectors.teeing(
                Collectors.reducing(BigInteger.ZERO, AbstractMap.SimpleEntry::getKey, BigInteger::add),
                Collectors.reducing(BigDecimal.ZERO, t -> t.getValue().multiply(new BigDecimal(t.getKey())), BigDecimal::add),
                (qty, total) -> new AbstractMap.SimpleEntry<>(qty, BigInteger.ZERO.equals(qty) ? BigDecimal.ZERO :
                        total.divide(new BigDecimal(qty), 4, RoundingMode.HALF_UP))
        ));
        PortfolioHolding holding = portfolioHoldingRepository.getById(holdingId);
        log.info("pnl {}", closedPnl);
        holding.setQuantity(sharesByAvgPrice.getKey());
        holding.setAveragePrice(sharesByAvgPrice.getValue());
        return portfolioHoldingRepository.save(holding);
    }

    private BigDecimal calculatePnl(BigInteger buySideShares, BigDecimal buyPrice,
                                    BigInteger sellSideShares, BigDecimal sellPrice) {
        BigInteger sharesDelta = buySideShares.subtract(sellSideShares);
        var pnlShares = new BigDecimal(sharesDelta.compareTo(BigInteger.ZERO) >= 0 ? sellSideShares : buySideShares);
        BigDecimal nextTotal = buyPrice.multiply(pnlShares);
        return sellPrice.multiply(pnlShares).subtract(nextTotal);
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
}
