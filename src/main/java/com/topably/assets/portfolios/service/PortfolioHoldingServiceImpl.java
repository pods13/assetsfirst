package com.topably.assets.portfolios.service;

import com.topably.assets.core.domain.TickerSymbol;
import com.topably.assets.instruments.domain.Instrument;
import com.topably.assets.portfolios.domain.Portfolio;
import com.topably.assets.portfolios.domain.PortfolioHolding;
import com.topably.assets.portfolios.domain.dto.PortfolioHoldingDto;
import com.topably.assets.portfolios.repository.PortfolioHoldingRepository;
import com.topably.assets.portfolios.repository.PortfolioRepository;
import com.topably.assets.trades.domain.TradeOperation;
import com.topably.assets.trades.domain.dto.add.AddTradeDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Collection;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PortfolioHoldingServiceImpl implements PortfolioHoldingService {

    private final PortfolioHoldingRepository portfolioHoldingRepository;
    private final PortfolioRepository portfolioRepository;

    @Override
    public Optional<PortfolioHolding> findByUsernameAndInstrumentId(String username, Long instrumentId) {
        return portfolioHoldingRepository.findByPortfolio_User_UsernameAndInstrument_Id(username, instrumentId);
    }

    @Override
    public PortfolioHolding managePortfolioHolding(AddTradeDto dto, Instrument tradedInstrument) {
        PortfolioHolding portfolioHolding = findByUsernameAndInstrumentId(dto.getUsername(), tradedInstrument.getId())
                .map(holding -> {
                    //TODO use FIFO rule for sell trades
                    BigInteger dtoQuantity = TradeOperation.SELL.equals(dto.getOperation()) ? dto.getQuantity().negate() : dto.getQuantity();
                    var holdingTotal = holding.getAveragePrice().multiply(new BigDecimal(holding.getQuantity()));
                    var tradeTotal = dto.getPrice().multiply(new BigDecimal(dtoQuantity));
                    var quantityTotal = holding.getQuantity().add(dtoQuantity);
                    var averagePrice = holdingTotal.add(tradeTotal)
                            .divide(new BigDecimal(quantityTotal), 4, RoundingMode.HALF_UP);
                    holding.setQuantity(quantityTotal);
                    holding.setAveragePrice(averagePrice);
                    return holding;
                })
                .orElseGet(() -> createHolding(dto, tradedInstrument));
        return portfolioHoldingRepository.save(portfolioHolding);
    }

    private PortfolioHolding createHolding(AddTradeDto dto, Instrument instrument) {
        Portfolio portfolio = portfolioRepository.findByUser_Username(dto.getUsername());
        return PortfolioHolding.builder()
                .portfolio(portfolio)
                .instrument(instrument)
                .quantity(dto.getQuantity())
                .averagePrice(dto.getPrice())
                .build();
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
