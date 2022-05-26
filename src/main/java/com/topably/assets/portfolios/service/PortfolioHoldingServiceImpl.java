package com.topably.assets.portfolios.service;

import com.topably.assets.auth.service.UserService;
import com.topably.assets.exchanges.domain.TickerSymbol;
import com.topably.assets.instruments.domain.Instrument;
import com.topably.assets.portfolios.domain.PortfolioHolding;
import com.topably.assets.portfolios.domain.dto.PortfolioHoldingDto;
import com.topably.assets.portfolios.repository.PortfolioHoldingRepository;
import com.topably.assets.trades.domain.TradeOperation;
import com.topably.assets.trades.domain.dto.add.AddTradeDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.Collection;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PortfolioHoldingServiceImpl implements PortfolioHoldingService {

    private final PortfolioHoldingRepository portfolioHoldingRepository;

    private final PortfolioService portfolioService;

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
                    var averagePrice = holdingTotal.add(tradeTotal).divide(new BigDecimal(quantityTotal), RoundingMode.HALF_UP);
                    holding.setQuantity(quantityTotal);
                    holding.setAveragePrice(averagePrice);
                    return holding;
                })
                .orElseGet(() -> createHolding(dto, tradedInstrument));
        return portfolioHoldingRepository.save(portfolioHolding);
    }

    private PortfolioHolding createHolding(AddTradeDto dto, Instrument instrument) {
        return PortfolioHolding.builder()
                .portfolio(portfolioService.findByUsername(dto.getUsername()))
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
                            .identifier(instrument.toTickerSymbol())
                            .currency(instrument.getExchange().getCurrency())
                            .quantity(holding.getQuantity())
                            .total(holding.getAveragePrice().multiply(new BigDecimal(holding.getQuantity())))
                            .build();
                }).toList();
    }
}
