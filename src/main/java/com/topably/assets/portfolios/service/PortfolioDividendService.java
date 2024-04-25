package com.topably.assets.portfolios.service;

import com.topably.assets.auth.domain.security.CurrentUser;
import com.topably.assets.findata.dividends.service.DividendService;
import com.topably.assets.instruments.domain.InstrumentType;
import com.topably.assets.portfolios.domain.dto.PubPortfolioDividendDto;
import com.topably.assets.portfolios.domain.position.PortfolioPosition;
import com.topably.assets.portfolios.mapper.PortfolioDividendMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.Set;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Transactional
public class PortfolioDividendService {

    private final PortfolioDividendMapper portfolioDividendMapper;
    private final PortfolioPositionService positionService;
    private final DividendService dividendService;
    private final PortfolioService portfolioService;

    public Page<PubPortfolioDividendDto> findUpcomingDividends(CurrentUser user, String identifier, Pageable pageable) {
        var portfolio = portfolioService.findPortfolioByIdentifier(user, identifier);
        var positions = positionService.findPortfolioPositionsByPortfolioId(portfolio.getId());
        var instrumentWithDividends = Set.of(InstrumentType.STOCK.name(), InstrumentType.ETF.name());
        var tickerByQuantity = positions.stream()
            .filter(p -> p.getQuantity().compareTo(BigInteger.ZERO) > 0)
            .filter(p -> instrumentWithDividends.contains(p.getInstrument().getInstrumentType()))
            .collect(Collectors.toMap(p -> p.getInstrument().toTicker(), PortfolioPosition::getQuantity));

        return dividendService.findUpcomingDividends(tickerByQuantity.keySet(), pageable)
            .map(d -> portfolioDividendMapper.modelToDto(d, tickerByQuantity.get(d.getInstrument().toTicker())));
    }

}
