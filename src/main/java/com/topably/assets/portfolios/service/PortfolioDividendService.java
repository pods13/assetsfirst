package com.topably.assets.portfolios.service;

import com.topably.assets.findata.dividends.service.DividendService;
import com.topably.assets.instruments.domain.InstrumentType;
import com.topably.assets.portfolios.domain.dto.PortfolioDividendDto;
import com.topably.assets.portfolios.domain.position.PortfolioPosition;
import com.topably.assets.portfolios.mapper.PortfolioDividendMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class PortfolioDividendService {

    private final PortfolioDividendMapper portfolioDividendMapper;
    private final PortfolioPositionService positionService;
    private final DividendService dividendService;

    public Page<PortfolioDividendDto> findUpcomingDividends(String identifier, Pageable pageable) {
        //TODO Get rid of hardcoded value
        var positions = positionService.findPortfolioPositionsByPortfolioId(1L);
        var instrumentWithDividends = Set.of(InstrumentType.STOCK.name(), InstrumentType.ETF.name());
        var tickerByQuantity = positions.stream()
            .filter(p -> instrumentWithDividends.contains(p.getInstrument().getInstrumentType()))
            .collect(Collectors.toMap(p -> p.getInstrument().toTicker(), PortfolioPosition::getQuantity));

        return dividendService.findUpcomingDividends(tickerByQuantity.keySet(), pageable)
            .map(d -> portfolioDividendMapper.modelToDto(d, tickerByQuantity.get(d.getInstrument().toTicker())));
    }
}
